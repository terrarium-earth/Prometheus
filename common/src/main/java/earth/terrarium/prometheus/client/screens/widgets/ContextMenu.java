package earth.terrarium.prometheus.client.screens.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public class ContextMenu extends AbstractWidget {

    private final Map<Component, Runnable> options = new LinkedHashMap<>();

    public ContextMenu() {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
        this.visible = false;
        this.active = false;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -1072689136, -804253680);
        int y = this.getY() + 5;
        for (Map.Entry<Component, Runnable> entry : this.options.entrySet()) {
            int x = this.getX() + 5;
            int width = Minecraft.getInstance().font.width(entry.getKey());
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + Minecraft.getInstance().font.lineHeight) {
                graphics.fill(x - 5, y - 2, x + width + 5, y + Minecraft.getInstance().font.lineHeight + 2, 0x80808080);
            }
            graphics.drawString(Minecraft.getInstance().font, entry.getKey(), x, y, 0xFFFFFFFF);
            y += Minecraft.getInstance().font.lineHeight;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int y = this.getY() + 5;
            for (Map.Entry<Component, Runnable> entry : this.options.entrySet()) {
                int x = this.getX() + 5;
                int width = Minecraft.getInstance().font.width(entry.getKey());
                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + Minecraft.getInstance().font.lineHeight) {
                    entry.getValue().run();
                    this.visible = false;
                    return true;
                }
                y += Minecraft.getInstance().font.lineHeight;
            }
            close();
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isActive();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public ContextMenu start(double x, double y) {
        this.options.clear();
        this.setX((int) x);
        this.setY((int) y);
        return this;
    }

    public ContextMenu addOption(Component component, Runnable runnable) {
        this.options.put(component, runnable);
        return this;
    }

    public ContextMenu open() {
        int longest = 0;
        for (Component component : this.options.keySet()) {
            int width = Minecraft.getInstance().font.width(component);
            if (width > longest) {
                longest = width;
            }
        }
        this.width = longest + 10;
        this.height = this.options.size() * Minecraft.getInstance().font.lineHeight + 10;
        this.visible = true;
        this.active = true;
        return this;
    }

    public ContextMenu close() {
        this.visible = false;
        this.active = false;
        return this;
    }
}
