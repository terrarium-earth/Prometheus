package earth.terrarium.prometheus.client.screens.roles.options.entries;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TextBoxListEntry extends ListEntry {

    protected String text;
    protected final int limit;
    protected final Component component;
    protected final Component tooltip;
    protected final Predicate<String> validator;

    public TextBoxListEntry(String text, int limit, Component component, Predicate<String> validator) {
        this(text, limit, component, CommonComponents.EMPTY, validator);
    }

    public TextBoxListEntry(String text, int limit, Component component, Component tooltip, Predicate<String> validator) {
        this.text = text;
        this.limit = limit;
        this.component = component;
        this.tooltip = tooltip;
        this.validator = validator;
    }

    @Override
    protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissor, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        graphics.drawString(
            Minecraft.getInstance().font,
            component, left + 6, top + 6, 0xFFFFFF,
            false
        );
        int color = selected ? this.validator.test(this.text) ? 0xFFFFFFFF : 0xFFFF0000 : 0x00000000;
        boolean show = getCount() < limit && selected && ((System.currentTimeMillis() / 500) % 2) == 0;
        MutableComponent text = Component.literal(getText());
        if (getCount() < limit) {
            text.append(Component.literal("_").withStyle(show ? ChatFormatting.RESET : ChatFormatting.BLACK));
        }
        renderTextBox(graphics, scissor, left + (width / 2) - 3, top + 3, (width / 2) - 3, 14, text, color);
        if (mouseX >= left + (width / 2) - 3 && mouseX < left + width - 6 && mouseY >= top + 3 && mouseY <= top + 17) {
            CursorUtils.setCursor(selected, CursorScreen.Cursor.TEXT);
        } else if (hovered && ComponentUtils.isTranslationResolvable(tooltip) && !tooltip.getString().isBlank()) {
            ScreenUtils.setTooltip(tooltip);
        }
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (getCount() < limit) {
            text += c;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 259 && getCount() > 0) {
            text = text.substring(0, text.length() - 1);
            return true;
        }
        if (Screen.isPaste(i)) {
            String text = Minecraft.getInstance().keyboardHandler.getClipboard();

            if (text.codePoints().count() + getCount() <= limit) {
                this.text += text;
            } else {
                this.text += text.substring(0, limit - getCount());
            }
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

    public String getText() {
        return text;
    }

    public int getCount() {
        return (int) text.codePoints().count();
    }

    public static void renderTextBox(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int height, Component text, int border) {
        graphics.fill(x, y, x + width, y + height, border);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF000000);
        try (var ignored = RenderUtils.createScissorBoxStack(scissor, Minecraft.getInstance(), graphics.pose(), x + 2, y + 1, width - 4, height - 2)) {

            int textY = (height - 2 - Minecraft.getInstance().font.lineHeight) / 2;

            int textWidth = Minecraft.getInstance().font.width(text);
            if (textWidth > width - 4) {
                int offset = textWidth - width + 4;
                graphics.drawString(
                    Minecraft.getInstance().font,
                    text, x + 3 - offset, y + 2 + textY, 0xFFFFFF,
                    false
                );
            } else {
                graphics.drawString(
                    Minecraft.getInstance().font,
                    text, x + 3, y + 2 + textY, 0xFFFFFF,
                    false
                );
            }
        }
    }
}