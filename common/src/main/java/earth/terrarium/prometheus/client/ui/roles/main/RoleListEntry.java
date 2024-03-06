package earth.terrarium.prometheus.client.ui.roles.main;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.olympus.client.components.lists.ListEntry;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;

public final class RoleListEntry implements ListEntry<Void> {

    private static final int BUTTON_WIDTH = 13;
    private static final int BUTTON_HEIGHT = 15;

    private static final int PADDING = 5;

    private final String displayName;
    private final LinearLayout buttons;

    public RoleListEntry(String displayName, Runnable moveUp, Runnable moveDown, Runnable edit, Runnable delete) {
        this.displayName = displayName;
        this.buttons = Util.make(LinearLayout.horizontal().spacing(PADDING), layout -> {
            if (moveUp != null) {
                layout.addChild(
                    new ImageButton(BUTTON_WIDTH, BUTTON_HEIGHT, UIConstants.LIST_UP, b -> moveUp.run(), CommonComponents.EMPTY)
                ).setTooltip(Tooltip.create(ConstantComponents.MOVE_UP));
            }

            if (moveDown != null) {
                layout.addChild(
                    new ImageButton(BUTTON_WIDTH, BUTTON_HEIGHT, UIConstants.LIST_DOWN, b -> moveDown.run(), CommonComponents.EMPTY)
                ).setTooltip(Tooltip.create(ConstantComponents.MOVE_DOWN));
            }

            if (edit != null) {
                layout.addChild(
                    new ImageButton(BUTTON_WIDTH, BUTTON_HEIGHT, UIConstants.LIST_EDIT, b -> edit.run(), CommonComponents.EMPTY)
                ).setTooltip(Tooltip.create(ConstantComponents.EDIT));
            }

            if (delete != null) {
                layout.addChild(
                    new ImageButton(BUTTON_WIDTH, BUTTON_HEIGHT, UIConstants.LIST_DELETE, b -> delete.run(), CommonComponents.EMPTY)
                ).setTooltip(Tooltip.create(UIConstants.DELETE));
            }
        });
        this.buttons.arrangeElements();
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        int height = this.getHeight(width);

        graphics.drawString(font, this.displayName, x + 4, (int) (y + (height - 9) / 2f), 0xFFFFFF);

        FrameLayout.centerInRectangle(this.buttons, x + width - this.buttons.getWidth(), y, this.buttons.getWidth(), height);
        this.buttons.visitWidgets(widget -> {
            widget.render(graphics, mouseX, mouseY, partialTicks);
            CursorUtils.setCursor(widget.isHovered(), CursorScreen.Cursor.POINTER);
        });
    }

    @Override
    public int getHeight(int width) {
        return BUTTON_HEIGHT + PADDING * 2;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        int height = this.getHeight(width);
        if (mouseX >= width - this.buttons.getWidth() && mouseX <= width && mouseY >= 0 && mouseY <= height) {
            FrameLayout.centerInRectangle(this.buttons, 0, 0, this.buttons.getWidth(), height);
            this.buttons.visitWidgets(widget ->
                widget.mouseClicked(mouseX - (width - this.buttons.getWidth()), mouseY, mouseButton)
            );
            return true;
        }
        return false;
    }

}
