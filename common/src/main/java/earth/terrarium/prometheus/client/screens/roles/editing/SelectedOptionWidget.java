package earth.terrarium.prometheus.client.screens.roles.editing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SelectedOptionWidget extends AbstractWidget {

    protected final OptionDisplayList list;

    public SelectedOptionWidget(int x, int y, int width, int height, OptionDisplayList list) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.list = list;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int i, int j, float f) {
        this.renderScrollingString(graphics, Minecraft.getInstance().font, 0, 0xffffff);
    }

    @Override
    public @NotNull Component getMessage() {
        return Component.translatable(this.list.getSelectedDisplay().toLanguageKey("option"));
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
