package earth.terrarium.prometheus.client.screens.roles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.roles.editing.RoleEditScreen;
import earth.terrarium.prometheus.client.utils.MouseLocationFix;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.RolesMenu;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.AddRolePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class RolesScreen extends AbstractContainerCursorScreen<RolesMenu> implements MenuAccess<RolesMenu> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/roles.png");

    private long timeSinceLastSaveWarning = 0;

    private Button saveButton;
    private Button undoButton;
    private Button moveUpButton;
    private Button moveDownButton;
    private Button editButton;
    private Button deleteButton;
    private RolesList list;

    private RolesList.Entry selected;

    public RolesScreen(RolesMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.passEvents = false;
        this.imageHeight = 212;
        this.imageWidth = 176;
    }

    @Override
    protected void init() {
        MouseLocationFix.fix(this.getClass());
        super.init();
        if (!this.menu.hasError()) {
            this.list = addRenderableWidget(new RolesList(this.leftPos + 8, this.topPos + 24, 144, 180, 20, item -> {
                this.selected = item;
                updateButtons();
            }));
            this.list.update(this.menu.getRoles());
        }

        this.saveButton = this.addRenderableWidget(button(this.leftPos + 154, this.topPos + 5, 17, 17, 176, 36, 17, CONTAINER_BACKGROUND, button -> {
            this.menu.save();
            updateButtons();
        }, ConstantComponents.SAVE));
        this.saveButton.active = false;

        this.undoButton = this.addRenderableWidget(button(this.leftPos + 154, this.topPos + 188, 17, 17, 193, 36, 17, CONTAINER_BACKGROUND, button -> {
            this.menu.reset();
            this.list.update(this.menu.getRoles());
            updateButtons();
        }, ConstantComponents.UNDO));
        this.undoButton.active = false;

        this.addRenderableWidget(button(this.leftPos + 157, this.topPos + 27, 12, 12, 176, 0, 12, CONTAINER_BACKGROUND, button -> {
            NetworkHandler.CHANNEL.sendToServer(new AddRolePacket());
        }, ConstantComponents.ADD)).active = !this.menu.hasError();

        this.deleteButton = this.addRenderableWidget(button(this.leftPos + 157, this.topPos + 43, 12, 12, 188, 0, 12, CONTAINER_BACKGROUND, button -> {
            if (selected != null) {
                this.menu.remove(selected.id());
                this.list.update(this.menu.getRoles());
                updateButtons();
            }
        }, ConstantComponents.REMOVE));
        this.deleteButton.active = false;

        this.moveUpButton = this.addRenderableWidget(button(this.leftPos + 157, this.topPos + 64, 12, 12, 200, 0, 12, CONTAINER_BACKGROUND, button -> {
            if (this.selected != null) {
                this.menu.move(this.selected.id(), true);
                this.list.update(this.menu.getRoles(), this.selected.id());
                updateButtons();
            }
        }, ConstantComponents.MOVE_UP));
        this.moveUpButton.active = false;

        this.moveDownButton = this.addRenderableWidget(button(this.leftPos + 157, this.topPos + 79, 12, 12, 212, 0, 12, CONTAINER_BACKGROUND, button -> {
            if (this.selected != null) {
                this.menu.move(this.selected.id(), false);
                this.list.update(this.menu.getRoles(), this.selected.id());
                updateButtons();
            }
        }, ConstantComponents.MOVE_DOWN));
        this.moveDownButton.active = false;

        this.editButton = this.addRenderableWidget(button(this.leftPos + 157, this.topPos + 99, 12, 12, 224, 0, 12, CONTAINER_BACKGROUND, button -> {
            if (this.saveButton.active) {
                this.timeSinceLastSaveWarning = System.currentTimeMillis();
            } else if (Minecraft.getInstance().gameMode != null && this.selected != null) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, this.menu.getIndexOf(this.selected.id()));
            }
        }, ConstantComponents.EDIT));
        this.editButton.active = false;
    }

    private void updateButtons() {
        if (this.saveButton == null) return;
        if (this.undoButton == null) return;
        if (this.deleteButton == null) return;
        if (this.moveUpButton == null) return;
        if (this.moveDownButton == null) return;
        if (this.editButton == null) return;

        int selectedIndex = this.list != null ? this.list.children().indexOf(this.selected) : -1;
        this.saveButton.active = this.undoButton.active = this.menu.areRolesDifferent();
        this.deleteButton.active = this.selected != null && selectedIndex < this.menu.getRoles().size() - 1;
        this.moveUpButton.active = this.selected != null && selectedIndex > 0 && selectedIndex < this.menu.getRoles().size() - 1;
        this.moveDownButton.active = this.selected != null && selectedIndex < this.menu.getRoles().size() - 2;
        this.editButton.active = this.selected != null;
    }

    @Override
    public void render(@NotNull PoseStack stack, int i, int j, float f) {
        this.renderBackground(stack);
        super.render(stack, i, j, f);
        this.renderTooltip(stack, i, j);
        if (this.menu.hasError()) {
            fill(stack, 0, 0, this.width, this.height, 0x88000000);
            drawCenteredString(stack, this.font, ConstantComponents.ERROR_IN_LOGS, this.width / 2, this.height / 2, 0xFF0000);
        }
    }

    @Override
    protected void renderLabels(@NotNull PoseStack stack, int i, int j) {
        Component title = this.saveButton.active && System.currentTimeMillis() - this.timeSinceLastSaveWarning < 2500 ? ConstantComponents.UNSAVED_CHANGES : this.title;
        this.font.draw(stack, title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, float f, int i, int j) {
        RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        blit(stack, k, l, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
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

    private static ImageButton button(int x, int y, int width, int height, int u, int v, int vOffset, ResourceLocation resourceLocation, Button.OnPress onPress, Component component) {
        ImageButton button = new ImageButton(x, y, width, height, u, v, vOffset, resourceLocation, onPress);
        button.setTooltip(Tooltip.create(component));
        return button;
    }
}