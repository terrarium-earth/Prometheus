package earth.terrarium.prometheus.client.screens.roles.editing;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.roles.RolesScreen;
import earth.terrarium.prometheus.client.utils.ClientUtils;
import earth.terrarium.prometheus.client.utils.MouseLocationFix;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import earth.terrarium.prometheus.common.menus.RoleEditMenu;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.SaveRolePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class RoleEditScreen extends AbstractContainerCursorScreen<RoleEditMenu> implements MenuAccess<RoleEditMenu> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/edit_role.png");
    private static final ResourceLocation BUTTONS = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/buttons.png");

    private QuickEditList list;
    private OptionDisplayList displayList;

    public RoleEditScreen(RoleEditMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.passEvents = false;
        this.imageHeight = 223;
        this.imageWidth = 276;
    }

    @Override
    protected void init() {
        MouseLocationFix.fix(this.getClass());
        super.init();

        this.list = addRenderableWidget(new QuickEditList(this.leftPos + 8, this.topPos + 29, 42, 180, 20, item -> {
            if (item != null && !item.id().equals(this.menu.getSelectedId())) {
                ClientUtils.sendClick(this, this.menu.getIndexOf(item.id()));
            }
        }));
        this.list.update(this.menu.getRoles());

        this.displayList = addRenderableWidget(new OptionDisplayList(this.leftPos + 56, this.topPos + 29, this.menu.getSelected()));
        this.displayList.setDisplay(CosmeticOptions.SERIALIZER.id());

        addRenderableOnly(new SelectedOptionWidget(this.leftPos + 77, this.topPos + 15, 170, 10, this.displayList));

        addRenderableWidget(new ImageButton(this.leftPos + 61, this.topPos + 15, 12, 12, 276, 0, 12, CONTAINER_BACKGROUND, 512, 512, button ->
            this.displayList.move(-1)
        )).setTooltip(Tooltip.create(ConstantComponents.PREV));

        addRenderableWidget(new ImageButton(this.leftPos + 251, this.topPos + 15, 12, 12, 288, 0, 12, CONTAINER_BACKGROUND, 512, 512, button ->
            this.displayList.move(1)
        )).setTooltip(Tooltip.create(ConstantComponents.NEXT));

        addRenderableWidget(new ImageButton(this.leftPos + 7, this.topPos + 7, 17, 17, 0, 97, 17, BUTTONS, button -> {
            if (Minecraft.getInstance().gameMode != null) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, -10);
            }
        })).setTooltip(Tooltip.create(ConstantComponents.BACK));

        addRenderableWidget(new ImageButton(this.leftPos + 34, this.topPos + 7, 17, 17, 18, 97, 17, BUTTONS, button -> {
            this.displayList.save(this.menu.getSelected());
            int index = this.menu.getIndexOf(this.menu.getSelectedId());
            if (index != -1) {
                this.menu.getRoles().set(index, new RoleEntry(this.menu.getSelectedId(), this.menu.getSelected()));
            }
            NetworkHandler.CHANNEL.sendToServer(new SaveRolePacket(this.menu.getSelectedId(), this.menu.getSelected()));
            this.list.update(this.menu.getRoles());
        })).setTooltip(Tooltip.create(ConstantComponents.SAVE));
    }

    @Override
    public void render(@NotNull PoseStack stack, int i, int j, float f) {
        this.renderBackground(stack);
        super.render(stack, i, j, f);
        this.renderTooltip(stack, i, j);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack stack, int i, int j) {
        if (this.menu.getSelected() == null) return;
        CosmeticOptions options = this.menu.getSelected().getOption(CosmeticOptions.SERIALIZER);
        if (options != null) {
            Component text = Component.translatable("prometheus.roles.edit", options.display());
            font.draw(stack, text, 162 - Mth.floor(font.width(text) / 2f), 6, 4210752);
        }
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, float f, int i, int j) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        blit(stack, k, l, 0, 0, this.imageWidth, this.imageHeight, 512, 512);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        //Ignore Closing
        if (this.minecraft.options.keyInventory.matches(i, j)) {
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void removed() {
        super.removed();
        MouseLocationFix.setFix(clas -> clas == RolesScreen.class || clas == RoleEditScreen.class);
    }
}