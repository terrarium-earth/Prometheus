package earth.terrarium.prometheus.client.screens.commands;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ClosingScissorBox;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MultilineEditBox extends AbstractScrollWidget {

    private final MultilineTextField text;
    private Function<String, Component> syntaxHighlighter = Component::nullToEmpty;

    public MultilineEditBox(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
        this.text = new MultilineTextField(Minecraft.getInstance().font, Integer.MAX_VALUE);
        this.text.setCursorListener(this::scrollToCursor);
    }

    @Override
    protected void renderContents(@NotNull PoseStack stack, int i, int j, float f) {
        ClosingScissorBox scissor = RenderUtils.createScissorBox(Minecraft.getInstance(), stack, getX(), getY() + innerPadding(), 15, this.height - this.innerPadding());
        stack.pushPose();
        stack.translate(0.0, -this.scrollAmount(), 0.0);
        for (int lineNum = 1; lineNum <= this.text.getLineCount(); lineNum++) {
            int lWidth = Minecraft.getInstance().font.width(String.valueOf(lineNum));
            Minecraft.getInstance().font.draw(stack, String.valueOf(lineNum), this.getX() + 14 - lWidth, this.getY() + this.innerPadding() + (lineNum - 1) * 9, 0x57546d);
        }
        stack.popPose();
        scissor.close();

        Gui.fill(stack, this.getX() + this.innerPadding() + 13, this.getY(), this.getX() + this.innerPadding() + 14, this.getY() + this.height, 0xff393747);

        scissor = RenderUtils.createScissorBox(Minecraft.getInstance(), stack, getX() + 15 + this.innerPadding(), getY(), this.width - 15 - (this.innerPadding() * 2), this.height);

        stack.pushPose();
        stack.translate(0.0, -this.scrollAmount(), 0.0);
        String string = this.text.value();
        if (!string.isEmpty()) {
            int k = this.text.cursor();

            int linesUpToCursor = this.text.value().substring(0, k).replaceAll("[^\\n]", "").length() + 1;
            var split = this.text.value().substring(0, k).split("\\R");
            int lineCursor = k > 0 && this.text.value().charAt(k - 1) == '\n' ? 0 : split[split.length - 1].length();

            int realWidth = this.width - 15;
            int avgCharWidth = Minecraft.getInstance().font.width("3".repeat(lineCursor + 1)) + (this.innerPadding() * 2);
            int xStart = this.getX() + 15 + this.innerPadding() - (avgCharWidth > realWidth ? avgCharWidth - realWidth : 0);

            int lineYStart = this.getY() + this.innerPadding();
            int line = 0;
            List<String> lines = new ArrayList<>(this.text.value().lines().toList());
            if (this.text.value().charAt(this.text.value().length() - 1) == '\n') {
                lines.add("");
            }
            for (String s : lines) {
                if (this.withinContentAreaTopBottom(lineYStart, lineYStart + 9) && !s.isBlank()) {
                    Minecraft.getInstance().font.drawShadow(stack, syntaxHighlighter.apply(s), (float) (xStart), (float) lineYStart, 0xffe0e0e0);
                }
                if (line == linesUpToCursor - 1) {
                    int start = Minecraft.getInstance().font.width(s.substring(0, lineCursor));
                    GuiComponent.fill(stack, xStart + start, lineYStart - 1, xStart + start + 1, lineYStart + 10, 0xffd0d0d0);
                }
                lineYStart += 9;
                line++;
            }

            if (this.text.hasSelection()) {
                MultilineTextField.StringView stringView2 = this.text.getSelected();
                int yStart = this.getY() + this.innerPadding();

                for (MultilineTextField.StringView stringView3 : this.text.iterateLines()) {
                    if (stringView2.beginIndex() <= stringView3.endIndex()) {
                        if (stringView3.beginIndex() > stringView2.endIndex()) {
                            break;
                        }

                        if (this.withinContentAreaTopBottom(yStart, yStart + 9)) {
                            int p = Minecraft.getInstance().font.width(string.substring(stringView3.beginIndex(), Math.max(stringView2.beginIndex(), stringView3.beginIndex())));
                            int q;
                            if (stringView2.endIndex() > stringView3.endIndex()) {
                                q = realWidth - this.innerPadding();
                            } else {
                                q = Minecraft.getInstance().font.width(string.substring(stringView3.beginIndex(), stringView2.endIndex()));
                            }

                            this.renderHighlight(stack, xStart + p, yStart, xStart + q, yStart + 9);
                        }
                    }
                    yStart += 9;
                }
            }

        }
        stack.popPose();
        scissor.close();
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.withinContentAreaPoint(d, e) && i == 0) {
            this.text.setSelecting(Screen.hasShiftDown());
            this.seekCursorScreen(d, e);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.withinContentAreaPoint(d, e) && i == 0) {
            this.text.setSelecting(true);
            this.seekCursorScreen(d, e);
            this.text.setSelecting(Screen.hasShiftDown());
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return this.text.keyPressed(i);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (this.visible && this.isFocused() && SharedConstants.isAllowedChatCharacter(c)) {
            this.text.insertText(Character.toString(c));
            return true;
        }
        return false;
    }

    @Override
    public int getInnerHeight() {
        return 9 * this.text.getLineCount();
    }

    @Override
    protected boolean scrollbarVisible() {
        return false;
    }

    @Override
    protected double scrollRate() {
        return 9.0 / 2.0;
    }

    private void renderHighlight(PoseStack poseStack, int i, int j, int k, int l) {
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        fill(poseStack, i, j, k, l, -16776961);
        RenderSystem.disableColorLogicOp();
    }

    private void scrollToCursor() {
        double d = this.scrollAmount();
        var view = this.text.getLineView((int) (d / 9.0));
        if (this.text.cursor() <= view.beginIndex()) {
            d = this.text.getLineAtCursor() * 9;
        } else {
            MultilineTextField.StringView stringView2 = this.text.getLineView((int) ((d + (double) this.height) / 9.0) - 1);
            if (this.text.cursor() > stringView2.endIndex()) {
                d = this.text.getLineAtCursor() * 9 - this.height + 9 + this.totalInnerPadding();
            }
        }

        this.setScrollAmount(d);
    }

    @Override
    public void renderWidget(@NotNull PoseStack stack, int x, int y, float partialTicks) {
        if (this.visible) {
            fill(stack, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xff393747);
            fill(stack, this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, 0xff1d1d26);
            this.renderContents(stack, x, y, partialTicks);
        }
    }

    private void seekCursorScreen(double x, double y) {
        double newX = x - 15 - (double) this.getX() - (double) this.innerPadding();
        double newY = y - (double) this.getY() - (double) this.innerPadding() + this.scrollAmount();
        this.text.seekCursorToPoint(newX, newY);
    }

    public void setValue(String string) {
        this.text.setValue(string);
        this.text.seekCursor(Whence.ABSOLUTE, 0);
    }

    public String getValue() {
        return this.text.value();
    }

    public void setSyntaxHighlighter(@NotNull Function<String, Component> syntaxHighlighter) {
        this.syntaxHighlighter = syntaxHighlighter;
    }

    public void setListener(Consumer<String> listener) {
        this.text.setValueListener(listener);
    }

}

