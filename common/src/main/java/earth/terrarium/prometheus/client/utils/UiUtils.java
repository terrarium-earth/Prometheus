package earth.terrarium.prometheus.client.utils;

import earth.terrarium.olympus.client.components.string.MultilineTextWidget;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;

public class UiUtils {

    public static <T extends AbstractWidget> T addLine(GridLayout layout, int index, int width, Component title, Int2ObjectFunction<T> factory) {
        LinearLayout line = LinearLayout.horizontal();

        line.addChild(
            MultilineTextWidget.create((int) (width * 0.7f), title).alignLeft(),
            layout.newCellSettings().alignVerticallyMiddle()
        );

        int widgetWidth = (int) (width * 0.3f);

        T widget = factory.apply(widgetWidth);

        int spacerWidth = widgetWidth - widget.getWidth();
        if (spacerWidth > 0) {
            line.addChild(new SpacerElement(spacerWidth, 0), layout.newCellSettings().alignVerticallyMiddle());
        }

        line.addChild(widget, layout.newCellSettings().alignVerticallyMiddle());

        layout.addChild(line, index, 0);
        return widget;
    }
}
