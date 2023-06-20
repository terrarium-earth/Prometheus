package earth.terrarium.prometheus.client.screens.widgets.editor;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.prometheus.client.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;

public class TextEditor implements Renderable, GuiEventListener, NarratableEntry {

    private final TextEditorContent content = new TextEditorContent();
    private final TextHighlighter highlighter;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private boolean focused = false;
    private int scroll = 0;

    private final int cursorColor;
    private final int lineNumColor;

    public TextEditor(int x, int y, int width, int height, int cursorColor, int lineNumColor, TextHighlighter highlighter) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.highlighter = highlighter;
        this.cursorColor = cursorColor;
        this.lineNumColor = lineNumColor;
    }

    public void setContent(String content) {
        this.content.lines().clear();
        this.content.lines().addAll(List.of(content.split("\n")));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        try (var ignored = RenderUtils.createScissor(Minecraft.getInstance(), graphics, this.x, this.y, this.width, this.height)) {
            int scrollPixels = scroll * 10;
            graphics.fill(this.x, this.y + content.cursor().y() * 10 - scrollPixels, this.x + this.width, this.y + content.cursor().y() * 10 + 10 - scrollPixels, 0x80808080);
            graphics.fill(this.x + 18, this.y, this.x + 19, this.y + this.height, lineNumColor | 0xFF000000);
            for (int i = 0; i < content.lines().size(); i++) {
                String lineNum = ClientUtils.getSmallNumber(i + 1);
                graphics.drawString(font, lineNum, this.x + 18 - font.width(lineNum), this.y + i * 10 + 1 - scrollPixels, lineNumColor | 0xFF000000, false);
            }
            try (var ignored1 = RenderUtils.createScissorBoxStack(ignored.stack(), Minecraft.getInstance(), graphics.pose(), this.x + 20, this.y, this.width - 20, this.height)) {
                try (var pose = new CloseablePoseStack(graphics)) {
                    String currentLine = content.line().substring(0, content.cursor().x());
                    int overflowXoffset = (Minecraft.getInstance().font.width(currentLine) - this.width + 25) + 10;
                    overflowXoffset = Math.max(0, overflowXoffset);

                    pose.translate(-overflowXoffset, -scrollPixels, 0);

                    boolean selecting;
                    for (int i = 0; i < content.lines().size(); i++) {
                        Vector2i selection = content.selection();
                        selecting = selection != null && i >= Math.min(content.cursor().y(), selection.y()) && i <= Math.max(content.cursor().y(), selection.y());

                        String line = content.lines().get(i);
                        graphics.drawString(font, this.highlighter.highlight(line), this.x + 20, this.y + i * 10 + 1, 0xFFFFFFFF, false);
                        if (i == content.cursor().y()) {
                            String first = line.substring(0, content.cursor().x());
                            var i1 = this.x + font.width(first) + 20;
                            if (System.currentTimeMillis() / 500 % 2 == 0) {
                                graphics.fill(i1, this.y + i * 10, i1 + 1, this.y + i * 10 + 10, cursorColor | 0xFF000000);
                            }
                        }

                        if (selecting) {
                            int x1 = 0;
                            int x2 = line.length();
                            if (i == selection.y() && i == content.cursor().y()) {
                                x1 = Math.min(content.cursor().x(), selection.x());
                                x2 = Math.max(content.cursor().x(), selection.x());
                            } else if (i == selection.y()) {
                                if (selection.y() < content.cursor().y()) {
                                    x1 = selection.x();
                                } else {
                                    x2 = selection.x();
                                }
                            } else if (i == content.cursor().y()) {
                                if (selection.y() < content.cursor().y()) {
                                    x2 = content.cursor().x();
                                } else {
                                    x1 = content.cursor().x();
                                }
                            }

                            if (x1 != x2) {
                                graphics.fill(this.x + 20 + font.width(line.substring(0, x1)), this.y + i * 10, this.x + 20 + font.width(line.substring(0, x2)), this.y + i * 10 + 10, 0x806464FF);
                            }
                        }
                    }
                }
            }
        }

        CursorUtils.setCursor(isMouseOver(mouseX, mouseY), CursorScreen.Cursor.TEXT);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (!isMouseOver(d, e)) return false;
        int cursorX = (int) (d - this.x - 20);
        int cursorY = Mth.clamp((int) (e - this.y) / 10, 0, content.lines().size() - 1);
        if (cursorY + scroll < 0 || cursorY + scroll >= content.lines().size()) return false;
        String line = content.lines().get(cursorY + scroll);
        String sub = Minecraft.getInstance().font.plainSubstrByWidth(line, cursorX);
        cursorX = Mth.clamp(sub.length(), 0, line.length());
        content.setCursor(cursorX, cursorY + scroll, Screen.hasShiftDown());
        return true;
    }

    @Override
    public boolean charTyped(char c, int i) {
        content.addChar(c);
        changeScroll(0);
        if (content.cursor().y() - scroll >= height / 10 - 1) {
            changeScroll(content.cursor().y() - scroll);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        var output = switch (i) {
            case InputConstants.KEY_DOWN -> {
                if (content.cursor().y() - scroll >= height / 10 - 1) changeScroll(1);
                content.moveY(1, Screen.hasShiftDown());
                yield true;
            }
            case InputConstants.KEY_UP -> {
                if (content.cursor().y() - scroll <= 1) changeScroll(-1);
                content.moveY(-1, Screen.hasShiftDown());
                yield true;
            }
            case InputConstants.KEY_LEFT -> {
                content.moveX(-1, Screen.hasShiftDown());
                yield true;
            }
            case InputConstants.KEY_RIGHT -> {
                content.moveX(1, Screen.hasShiftDown());
                yield true;
            }
            case InputConstants.KEY_RETURN -> {
                content.newline();
                if (content.cursor().y() - scroll >= height / 10 - 1) changeScroll(1);
                yield true;
            }
            case InputConstants.KEY_BACKSPACE -> {
                content.backspace();
                yield true;
            }
            default -> {
                if (Screen.isCut(i)) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(content.getSelectedText());
                    content.deleteSelection();
                    yield true;
                } else if (Screen.isCopy(i)) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(content.getSelectedText());
                    yield true;
                } else if (Screen.isPaste(i)) {
                    content.addText(Minecraft.getInstance().keyboardHandler.getClipboard());
                    yield true;
                } else if (Screen.isSelectAll(i)) {
                    content.setCursor(0, 0, false);
                    content.setCursor(content.lines().get(content.lines().size() - 1).length(), content.lines().size() - 1, true);
                    yield true;
                }
                yield false;
            }
        };
        changeScroll(0);
        return output;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        changeScroll((int) -f);
        return true;
    }

    private void changeScroll(int amount) {
        scroll = Math.max(0, Math.min(content.lines().size() - height / 10, scroll + amount));
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return d >= this.x && d <= this.x + this.width && e >= this.y && e <= this.y + this.height;
    }

    @Override
    public void setFocused(boolean bl) {
        this.focused = bl;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    public List<String> lines() {
        return content.lines();
    }
}