package earth.terrarium.prometheus.client.ui.roles.main;

import earth.terrarium.olympus.client.components.buttons.TextButton;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundAddRolePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class RolesScreen extends BaseModal {

    private final RolesContent content;
    private RolesList list;

    public RolesScreen(RolesContent content, Screen screen) {
        super(CommonComponents.EMPTY, screen);
        this.content = content;

    }

    @Override
    protected void init() {
        super.init();

        LinearLayout layout = LinearLayout.vertical().spacing(5);

        LinearLayout header = LinearLayout.horizontal();

        int width = this.modalContentWidth - 20;

        header.addChild(new SpacerElement(width / 2, 0));

        header.addChild(new SpacerElement(width / 2 - 30, 0));
        header.addChild(TextButton.create(
            30, 20,
            ConstantComponents.ADD,
            b -> NetworkHandler.CHANNEL.sendToServer(new ServerboundAddRolePacket())
        )).active = !this.content.hasError();

        header.arrangeElements();
        layout.addChild(header);

        this.list = layout.addChild(new RolesList(this.list, width, this.modalContentHeight - header.getHeight() - 5, this.content));
        this.list.update();

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft + 10, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected GridLayout initButtons(int position) {
        GridLayout layout = super.initButtons(position + 1);
        layout.addChild(
            new ImageButton(11, 11, UIConstants.MODAL_SAVE, b -> {
                this.content.save();
                this.list.update();
            }, ConstantComponents.SAVE),
            0, position
        ).setTooltip(Tooltip.create(ConstantComponents.SAVE));
        return layout;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(graphics);
        graphics.blitSprite(UIConstants.MODAL, this.left, this.top, this.modalWidth, this.modalHeight);
        graphics.blitSprite(UIConstants.MODAL_HEADER, this.left, this.top, this.modalWidth, TITLE_BAR_HEIGHT);
    }

    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.content.areRolesDifferent()) return;
        graphics.drawString(
            this.font,
            ConstantComponents.UNSAVED_CHANGES, this.left + PADDING, (int) (this.top + (TITLE_BAR_HEIGHT - 9) / 2f) + 1,
            0xffffffff, false
        );
    }

    public static void open(RolesContent content) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof RolesScreen) {
            screen = null;
        }
        Minecraft.getInstance().setScreen(new RolesScreen(content, screen));
    }
}
