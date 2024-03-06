package earth.terrarium.prometheus.client.ui.roles.adding;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.olympus.client.components.lists.ListEntry;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.CommonComponents;

import java.util.UUID;

public final class MemberRoleListEntry implements ListEntry<UUID> {

    private static final int BUTTON_WIDTH = 13;
    private static final int BUTTON_HEIGHT = 15;

    private static final int PADDING = 5;
    private static final int SPACE = BUTTON_WIDTH;

    private final String displayName;
    private final GridLayout buttons;

    public MemberRoleListEntry(String displayName, UUID id, Object2BooleanMap<UUID> roles, Runnable saver) {
        this.displayName = displayName;
        this.buttons = new GridLayout().columnSpacing(PADDING);
        this.buttons.addChild(
            new ImageButton(
                BUTTON_WIDTH, BUTTON_HEIGHT,
                UIConstants.LIST_DELETE, b -> {
                    roles.put(id, false);
                    saver.run();
                },
                CommonComponents.EMPTY
            ),
            0, 0
        ).setTooltip(Tooltip.create(ConstantComponents.REMOVE));
        this.buttons.arrangeElements();
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        graphics.drawString(font, this.displayName, x + 4, y + 4, 0xFFFFFF);

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
