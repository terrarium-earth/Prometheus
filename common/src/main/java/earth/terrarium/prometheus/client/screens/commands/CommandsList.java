package earth.terrarium.prometheus.client.screens.commands;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.ServerboundDeleteCommandPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommandsList extends SelectionList<CommandsList.CommandEntry> {

    public CommandsList(int x, int y, int width, int height, Consumer<@Nullable String> onSelection) {
        super(x, y, width, height, 13, entry -> onSelection.accept(entry == null || entry.deleted ? null : entry.command), true);
    }

    public void update(String selected, List<String> commands) {
        CommandEntry selectedEntry = null;
        List<CommandEntry> entries = new ArrayList<>();
        for (String command : commands) {
            CommandEntry entry = new CommandEntry(command);
            entries.add(entry);
            if (command.equals(selected)) {
                selectedEntry = entry;
            }
        }
        updateEntries(entries);
        setSelected(selectedEntry);
    }

    public void add(String command) {
        if (children().stream().noneMatch(entry -> entry.command.equals(command))) {
            CommandEntry entry = new CommandEntry(command);
            addEntry(entry);
            setSelected(entry);
        }
    }

    public static class CommandEntry extends ListEntry {

        private final String command;
        private int lastWidth = -1;
        private boolean deleted = false;

        public CommandEntry(String command) {
            this.command = command;
        }

        @Override
        protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            lastWidth = width;
            if (hovered) {
                graphics.fill(left, top, left + width, top + height, 0x80FFFFFF);
            } else if (selected) {
                graphics.fill(left, top, left + width, top + height, 0x80808080);
            }
            MutableComponent component = Component.literal("\uD83D\uDDF3 " + command);
            if (deleted) {
                component = component.withStyle(ChatFormatting.STRIKETHROUGH);
            }
            Font font = Minecraft.getInstance().font;
            graphics.drawString(font, component, left + 5, top + 2, 0x404040, false);

            boolean deleteHovered = hovered && mouseX >= left + width - 18 && mouseX <= left + width - 5 && mouseY >= top && mouseY <= top + 13;

            graphics.drawString(font, "x", left + width - font.width("x") - 5, top + 2, deleteHovered ? 0x804040 : 0x404040, false);
            if (deleteHovered) {
                ScreenUtils.setTooltip(Component.literal("Delete command"));
            }

            CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (i == 0 && d >= lastWidth - 18 && d <= lastWidth - 5) {
                this.deleted = true;
                NetworkHandler.CHANNEL.sendToServer(new ServerboundDeleteCommandPacket(command));
                return true;
            }
            return false;
        }

        @Override
        public void setFocused(boolean bl) {}

        @Override
        public boolean isFocused() {
            return false;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }
    }

}
