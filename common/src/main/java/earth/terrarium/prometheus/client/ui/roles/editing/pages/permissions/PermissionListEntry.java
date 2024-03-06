package earth.terrarium.prometheus.client.ui.roles.editing.pages.permissions;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.buttons.TriStateButton;
import earth.terrarium.olympus.client.components.lists.ListEntry;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Map;

public final class PermissionListEntry implements ListEntry<ObjectObjectMutablePair<String, TriState>> {

    private static final int BUTTON_WIDTH = 13;
    private static final int BUTTON_HEIGHT = 15;

    private static final int PADDING = 5;
    private static final int SPACE = BUTTON_WIDTH * 2 + PADDING;

    private final ObjectObjectMutablePair<String, TriState> pair;
    private final GridLayout buttons;

    public PermissionListEntry(ObjectObjectMutablePair<String, TriState> pair, Map<String, TriState> permissions, Runnable saver) {
        this.pair = pair;
        this.buttons = Util.make(new GridLayout().columnSpacing(PADDING), layout -> {
            layout.addChild(
                new TriStateButton(BUTTON_WIDTH, BUTTON_HEIGHT, b -> {
                    TriState state = pair.right();
                    if (state.isUndefined()) pair.right(TriState.TRUE);
                    else if (state.isTrue()) pair.right(TriState.FALSE);
                    else pair.right(TriState.UNDEFINED);

                    permissions.put(pair.left(), pair.right());
                    saver.run();
                }, pair::right),
                0, 0
            );
            layout.addChild(
                new ImageButton(BUTTON_WIDTH, BUTTON_HEIGHT,
                    UIConstants.LIST_DELETE, b -> {
                    permissions.remove(pair.key());
                    saver.run();
                }, CommonComponents.EMPTY
                ),
                0, 1
            ).setTooltip(Tooltip.create(ConstantComponents.REMOVE));
        });
        this.buttons.arrangeElements();
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        String id = this.pair.left();
        Font font = Minecraft.getInstance().font;
        Component text = Component.translatableWithFallback(id + "." + "permission", id);

        graphics.drawString(font, text, x + 4, y + 4, 0xFFFFFF);

        FrameLayout.centerInRectangle(this.buttons, x + width - SPACE, y, SPACE, 18);
        this.buttons.visitWidgets(widget -> {
            widget.render(graphics, mouseX, mouseY, partialTicks);
            CursorUtils.setCursor(widget.isHovered(), CursorScreen.Cursor.POINTER);
        });
    }

    @Override
    public int getHeight(int width) {
        return 18;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        int height = this.getHeight(width);
        if (mouseX >= width - SPACE && mouseX <= width && mouseY >= 0 && mouseY <= height) {
            FrameLayout.centerInRectangle(this.buttons, 0, 0, SPACE, height);
            this.buttons.visitWidgets(widget ->
                widget.mouseClicked(mouseX - (width - SPACE), mouseY, mouseButton)
            );
            return true;
        }
        return false;
    }

}
