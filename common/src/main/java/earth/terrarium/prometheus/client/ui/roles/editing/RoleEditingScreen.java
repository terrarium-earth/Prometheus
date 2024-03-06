package earth.terrarium.prometheus.client.ui.roles.editing;

import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.client.ui.roles.editing.pages.OptionsPage;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundSaveRolePacket;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;

public class RoleEditingScreen extends BaseModal {

    private final RoleEditContent content;
    private final Page page;

    public RoleEditingScreen(RoleEditContent content, BiFunction<RoleEditContent, Runnable, Page> page, Screen screen) {
        super(getTitle(content), screen);
        this.content = content;

        this.page = page.apply(content, this::rebuildWidgets);
    }

    @Override
    protected void init() {
        super.init();

        LinearLayout layout = LinearLayout.vertical();

        layout.addChild(this.page.getContents(this.modalContentWidth - 20, this.modalContentHeight));

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft + 10, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected GridLayout initButtons(int position) {
        if (page.canSave()) {
            GridLayout layout = super.initButtons(position + 1);
            layout.addChild(
                new ImageButton(11, 11, UIConstants.MODAL_SAVE, b -> {
                    this.page.save(this.content.selected());
                    NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveRolePacket(this.content.selectedId(), this.content.selected()));
                }, ConstantComponents.SAVE),
                0, position
            ).setTooltip(Tooltip.create(ConstantComponents.SAVE));
            return layout;
        }
        return super.initButtons(position);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(graphics);
        graphics.blitSprite(UIConstants.MODAL, this.left, this.top, this.modalWidth, this.modalHeight);
        graphics.blitSprite(UIConstants.MODAL_HEADER, this.left, this.top, this.modalWidth, TITLE_BAR_HEIGHT);
    }

    private static Component getTitle(RoleEditContent content) {
        CosmeticOptions options = content.selected().getOption(CosmeticOptions.SERIALIZER);
        if (options == null) return CommonComponents.EMPTY;
        return Component.translatable("prometheus.roles.edit", options.display());
    }

    public static void open(RoleEditContent content) {
        open(content, OptionsPage::new);
    }

    public static void open(RoleEditContent content, BiFunction<RoleEditContent, Runnable, Page> page) {
        Minecraft.getInstance().setScreen(new RoleEditingScreen(content, page, Minecraft.getInstance().screen));
    }
}
