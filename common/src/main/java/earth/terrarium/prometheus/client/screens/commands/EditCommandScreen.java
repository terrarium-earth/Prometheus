package earth.terrarium.prometheus.client.screens.commands;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommand;
import earth.terrarium.prometheus.common.menus.EditCommandMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EditCommandScreen extends AbstractContainerCursorScreen<EditCommandMenu> implements MenuAccess<EditCommandMenu> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/edit_command.png");
    private static final ResourceLocation BUTTONS = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/buttons.png");

    private MultilineEditBox box;
    private ImageButton save;
    private ImageButton undo;

    public EditCommandScreen(EditCommandMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        this.imageHeight = 223;
        this.imageWidth = 276;
    }

    @Override
    protected void init() {
        super.init();
        box = addRenderableWidget(new MultilineEditBox(this.leftPos + 8, this.topPos + 29, 260, 180, CommonComponents.EMPTY));
        box.setValue(String.join("\n", this.menu.lines()));
        box.setSyntaxHighlighter(this::highlight);
        box.setListener((text) -> updateButtons());
        save = addRenderableWidget(new ImageButton(this.leftPos + 252, this.topPos + 7, 17, 17, 18, 97, 17, BUTTONS, 256, 256, (button) -> {
            this.menu.saveLines(List.of(box.getValue().split("\\R")));
            updateButtons();
        }));
        save.setTooltip(Tooltip.create(ConstantComponents.SAVE));
        undo = addRenderableWidget(new ImageButton(this.leftPos + 231, this.topPos + 7, 17, 17, 36, 97, 17, BUTTONS, 256, 256, (button) -> {
            box.setValue(String.join("\n", this.menu.lines()));
            updateButtons();
        }));
        undo.setTooltip(Tooltip.create(ConstantComponents.UNDO));
        updateButtons();
    }

    @Override
    protected void renderLabels(@NotNull PoseStack stack, int i, int j) {
        this.font.draw(stack, Component.translatable("prometheus.commands.edit", this.menu.id()), (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, float f, int i, int j) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        blit(stack, k, l, 0, 0, this.imageWidth, this.imageHeight, 512, 512);
    }

    @Override
    public void render(@NotNull PoseStack stack, int i, int j, float f) {
        super.render(stack, i, j, f);
        for (GuiEventListener child : children()) {
            if (child instanceof AbstractWidget widget && widget.isHovered()) {
                if (widget.active) {
                    if (widget instanceof MultilineEditBox) {
                        super.setCursor(Cursor.TEXT);
                    } else {
                        super.setCursor(Cursor.POINTER);
                    }
                } else {
                    super.setCursor(Cursor.DEFAULT);
                }
                break;
            }
        }
    }

    @Override
    public void setCursor(Cursor cursor) {

    }

    public void updateButtons() {
        if (save != null && undo != null && box != null) {
            save.active = !String.join("\n", this.menu.lines()).equals(box.getValue());
            undo.active = !String.join("\n", this.menu.lines()).equals(box.getValue());
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.minecraft.options.keyInventory.matches(i, j)) return false;
        return super.keyPressed(i, j, k);
    }

    private static final Pattern NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");

    private Component highlight(String text) {
        if (text.startsWith("#")) {
            return Component.literal(text).withStyle(style -> style.withColor(0x5b6668));
        }
        List<Component> components = new ArrayList<>();
        String[] split = text.split(" ");
        boolean quote = false;
        for (String part : split) {
            if (DynamicCommand.USER_PARAMETER_PATTERN.matcher(part).matches()) {
                components.add(Component.literal(part).withStyle(ChatFormatting.BLUE));
            } else if (DynamicCommand.CUSTOM_PARAMETER_PATTERN.matcher(part).matches()) {
                components.add(Component.literal(part).withStyle(ChatFormatting.GOLD));
            } else if (components.isEmpty()) {
                components.add(Component.literal(part).withStyle(Style.EMPTY.withColor(0xdf946c)));
            } else if (part.contains("\"")) {
                List<Component> tempComponents = new ArrayList<>();
                StringBuilder theText = new StringBuilder();
                for (char c : part.toCharArray()) {
                    if (c == '"') {
                        if (quote) {
                            theText.append(c);
                            tempComponents.add(Component.literal(formatArgs(theText.toString())).withStyle(Style.EMPTY.withColor(0x618941)));
                            theText = new StringBuilder();
                        } else if (theText.length() > 0) {
                            tempComponents.add(Component.literal(formatArgs(theText.toString())).withStyle(Style.EMPTY.withColor(0x94b8d0)));
                            theText = new StringBuilder();
                            theText.append(c);
                        } else {
                            theText.append(c);
                        }
                        quote = !quote;
                    } else {
                        theText.append(c);
                    }
                }
                if (theText.length() > 0) {
                    tempComponents.add(Component.literal(formatArgs(theText.toString())).withStyle(Style.EMPTY.withColor(quote ? 0x618941 : 0x94b8d0)));
                }
                components.add(ComponentUtils.formatList(tempComponents, CommonComponents.EMPTY));
            } else if (quote) {
                components.add(Component.literal(part).withStyle(Style.EMPTY.withColor(0x618941)));
            } else if (NUMBER.matcher(part).find()) {
                List<Component> tempComponents = new ArrayList<>();
                var matcher = NUMBER.matcher(part);
                int last = 0;
                while (matcher.find()) {
                    if (matcher.start() > last) {
                        tempComponents.add(Component.literal(part.substring(last, matcher.start())).withStyle(Style.EMPTY.withColor(0x94b8d0)));
                    }
                    tempComponents.add(Component.literal(part.substring(matcher.start(), matcher.end())).withStyle(Style.EMPTY.withColor(0x49abda)));
                    last = matcher.end();
                }
                if (last < part.length()) {
                    tempComponents.add(Component.literal(part.substring(last)).withStyle(Style.EMPTY.withColor(0x94b8d0)));
                }
                components.add(ComponentUtils.formatList(tempComponents, CommonComponents.EMPTY));
            }else {
                components.add(Component.literal(formatArgs(part)).withStyle(Style.EMPTY.withColor(0x94b8d0)));
            }
        }

        return ComponentUtils.formatList(components, CommonComponents.SPACE);
    }

    private static String formatArgs(String text) {
        text = DynamicCommand.USER_PARAMETER_PATTERN.matcher(text).replaceAll("§9$0§r");
        text = DynamicCommand.CUSTOM_PARAMETER_PATTERN.matcher(text).replaceAll("§6$0§r");
        return text;
    }
}
