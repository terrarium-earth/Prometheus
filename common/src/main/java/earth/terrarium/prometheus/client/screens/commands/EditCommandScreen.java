package earth.terrarium.prometheus.client.screens.commands;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.widgets.editor.TextEditor;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.OpenCommandPacket;
import earth.terrarium.prometheus.common.network.messages.server.SaveCommandPacket;
import earth.terrarium.prometheus.mixin.client.accessors.FontManagerAccessor;
import earth.terrarium.prometheus.mixin.client.accessors.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class EditCommandScreen extends BaseCursorScreen {

    public static final ResourceLocation HEADING = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/heading.png");
    private static final Font FONT = new Font(id -> ((FontManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(Prometheus.MOD_ID, "monocraft")), false);

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
                NetworkHandler.CHANNEL.sendToServer(new OpenCommandPacket(command));
            }
        }));
        list.update(this.command, commands);

        this.addBox = addRenderableWidget(new EditBox(this.font, 2, 2, sidebar - 15, 10, CommonComponents.EMPTY));
        this.addBox.setMaxLength(100);
        this.addBox.setBordered(false);
        this.addBox.setTextColor(-1);

        // Buttons

        addRenderableWidget(new ImageButton(sidebar - 12, 1, 11, 11, 245, 0, 11, HEADING, 256, 256, (button) -> {
            Set<String> commands = this.commands.stream().map(String::toLowerCase).collect(java.util.stream.Collectors.toSet());
            boolean invalid = commands.contains(this.addBox.getValue().toLowerCase()) || this.addBox.getValue().isEmpty();
            if (!invalid) {
                NetworkHandler.CHANNEL.sendToServer(new OpenCommandPacket(this.addBox.getValue().toLowerCase()));
            }
        })).setTooltip(Tooltip.create(Component.literal("Add")));

        addRenderableWidget(new ImageButton(this.width - 38, 1, 11, 11, 245, 88, 11, HEADING, 256, 256, (button) -> {
            editor.setContent(String.join("\n", lines));
            if (list.getSelected().isDeleted()) {
                NetworkHandler.CHANNEL.sendToServer(new SaveCommandPacket(command, editor.lines()));
                list.getSelected().setDeleted(false);
            }
        })).setTooltip(Tooltip.create(Component.literal("Undo")));

        addRenderableWidget(new ImageButton(this.width - 25, 1, 11, 11, 245, 66, 11, HEADING, 256, 256, (button) -> {
            NetworkHandler.CHANNEL.sendToServer(new SaveCommandPacket(command, editor.lines()));
            list.add(command);
        })).setTooltip(Tooltip.create(Component.literal("Save")));

        addRenderableWidget(new ImageButton(this.width - 12, 1, 11, 11, 245, 22, 11, HEADING, 256, 256, (button) -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.setScreen(null);
            }
        })).setTooltip(Tooltip.create(Component.literal("Close")));
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        graphics.fill(0, 0, width, height, 0xD0000000);
        graphics.blitRepeating(HEADING, 0, 0, this.width, 15, 0, 0, 128, 15);
        int sidebar = (int) (this.width * 0.25f) - 2;
        graphics.blitRepeating(HEADING, sidebar, 15, 2, this.height - 15, 243, 0, 2, 256);
        graphics.blitRepeating(HEADING,
            0, 15,
            sidebar, this.height - 15,
            0, 15,
            122, 241
        );
        graphics.blitRepeating(HEADING,
            sidebar + 2, 15,
            this.width - sidebar, this.height - 15,
            122, 15,
            121, 241
        );
        int textX = (int) (this.width * 0.25f) + ((int) (this.width * 0.75f) / 2) - font.width("Command - " + command) / 2;
        graphics.drawString(
            font,
            "Command - " + command, textX, 3, 0x404040,
            false
        );

        Set<String> commands = this.commands.stream().map(String::toLowerCase).collect(java.util.stream.Collectors.toSet());

        boolean invalid = (commands.contains(this.addBox.getValue().toLowerCase()) || this.addBox.getValue().isEmpty()) && this.addBox.isFocused();

        graphics.fill(1, 11, sidebar - 13, 12, invalid ? 0xFFFF8080 : 0xFFFFFFFF);

        super.render(graphics, i, j, f);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
