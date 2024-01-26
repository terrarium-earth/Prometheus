package earth.terrarium.prometheus.client.screens.commands;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.widgets.editor.TextEditor;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.ServerboundOpenCommandPacket;
import earth.terrarium.prometheus.common.network.messages.server.ServerboundSaveCommandPacket;
import earth.terrarium.prometheus.mixin.client.accessors.FontManagerAccessor;
import earth.terrarium.prometheus.mixin.client.accessors.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class EditCommandScreen extends BaseCursorScreen {

    private static final Font FONT = new Font(id -> ((FontManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(Prometheus.MOD_ID, "monocraft")), false);

    private static final WidgetSprites ADD_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "heading/add_button"),
        new ResourceLocation(Prometheus.MOD_ID, "heading/add_button_highlighted")
    );

    private static final WidgetSprites CLOSE_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "heading/close_button"),
        new ResourceLocation(Prometheus.MOD_ID, "heading/close_button_highlighted")
    );

    private static final WidgetSprites SAVE_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "heading/save_button"),
        new ResourceLocation(Prometheus.MOD_ID, "heading/save_button_highlighted")
    );

    private static final WidgetSprites UNDO_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "heading/undo_button"),
        new ResourceLocation(Prometheus.MOD_ID, "heading/undo_button_highlighted")
    );

    private final List<String> commands = new ArrayList<>();
    private final List<String> lines = new ArrayList<>();
    private final String command;
    private final CommandEditorTheme.Theme theme = CommandEditorTheme.getTextTheme();

    private EditBox addBox;

    public EditCommandScreen(Collection<String> commands, Collection<String> lines, String command) {
        super(CommonComponents.EMPTY);
        this.commands.addAll(commands);
        this.lines.addAll(lines);
        this.command = command;
    }

    public static void open(Collection<String> commands, Collection<String> lines, String command) {
        Minecraft.getInstance().setScreen(new EditCommandScreen(commands, lines, command));
    }

    @Override
    protected void init() {
        int sidebar = (int) (this.width * 0.25f) - 2;
        TextEditor editor = addRenderableWidget(new TextEditor(
            sidebar + 2, 15, this.width - sidebar + 2, this.height - 15,
            this.theme.cursor(),
            this.theme.lineNums(),
            FONT,
            new CommandsHighlighter(this.theme)
        ));
        editor.setContent(String.join("\n", lines));
        CommandsList list = addRenderableWidget(new CommandsList(0, 15, sidebar, this.height - 15, command -> {
            if (command != null && !command.equals(this.command)) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenCommandPacket(command));
            }
        }));
        list.update(this.command, commands);

        this.addBox = addRenderableWidget(new EditBox(this.font, 2, 2, sidebar - 15, 10, CommonComponents.EMPTY));
        this.addBox.setMaxLength(100);
        this.addBox.setBordered(false);
        this.addBox.setTextColor(-1);

        // Buttons

        addRenderableWidget(new ImageButton(sidebar - 12, 1, 11, 11, ADD_BUTTON_SPRITES, (button) -> {
            Set<String> commands = this.commands.stream().map(String::toLowerCase).collect(java.util.stream.Collectors.toSet());
            boolean invalid = commands.contains(this.addBox.getValue().toLowerCase()) || this.addBox.getValue().isEmpty();
            if (!invalid) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenCommandPacket(this.addBox.getValue().toLowerCase()));
            }
        })).setTooltip(Tooltip.create(Component.literal("Add")));

        addRenderableWidget(new ImageButton(this.width - 38, 1, 11, 11, UNDO_BUTTON_SPRITES, (button) -> {
            editor.setContent(String.join("\n", lines));
            if (list.getSelected().isDeleted()) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveCommandPacket(command, editor.lines()));
                list.getSelected().setDeleted(false);
            }
        })).setTooltip(Tooltip.create(Component.literal("Undo")));

        addRenderableWidget(new ImageButton(this.width - 25, 1, 11, 11, SAVE_BUTTON_SPRITES, (button) -> {
            NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveCommandPacket(command, editor.lines()));
            list.add(command);
        })).setTooltip(Tooltip.create(Component.literal("Save")));

        addRenderableWidget(new ImageButton(this.width - 12, 1, 11, 11, CLOSE_BUTTON_SPRITES, (button) -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.setScreen(null);
            }
        })).setTooltip(Tooltip.create(Component.literal("Close")));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xD0000000);
        graphics.blitSprite(new ResourceLocation(Prometheus.MOD_ID, "commands/header"), 0, 0, this.width, 15);
        int sidebar = (int) (this.width * 0.25f) - 2;
        graphics.blitSprite(new ResourceLocation(Prometheus.MOD_ID, "commands/divider"), sidebar, 0, 2, this.height);
        graphics.blitSprite(new ResourceLocation(Prometheus.MOD_ID, "commands/light_background"), 0, 15, sidebar, this.height - 15);
        graphics.blitSprite(new ResourceLocation(Prometheus.MOD_ID, "commands/dark_background"), sidebar + 2, 15, this.width - sidebar - 2, this.height - 15);
        int textX = (int) (this.width * 0.25f) + ((int) (this.width * 0.75f) / 2) - font.width("Command - " + command) / 2;
        graphics.drawString(
            font,
            "Command - " + command, textX, 3, 0x404040,
            false
        );

        Set<String> commands = this.commands.stream().map(String::toLowerCase).collect(java.util.stream.Collectors.toSet());

        boolean invalid = (commands.contains(this.addBox.getValue().toLowerCase()) || this.addBox.getValue().isEmpty()) && this.addBox.isFocused();

        graphics.fill(1, 11, sidebar - 13, 12, invalid ? 0xFFFF8080 : 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
