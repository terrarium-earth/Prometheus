package earth.terrarium.prometheus.client.screens.roles.adding;

import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.MemberRolesMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MemberRolesScreen extends AbstractContainerCursorScreen<MemberRolesMenu> implements MenuAccess<MemberRolesMenu> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/roles.png");
    private static final ResourceLocation BUTTONS = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/buttons.png");

    private Button saveButton;
    private Button undoButton;
    private MemberRolesList list;

    public MemberRolesScreen(MemberRolesMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 212;
        this.imageWidth = 176;
    }

    @Override
    protected void init() {
        super.init();

        this.list = this.addRenderableWidget(new MemberRolesList(this.leftPos + 8, this.topPos + 24, 144, 180, 20, item -> updateButtons()));
        this.list.update(this.menu.getRoles());

        this.saveButton = this.addRenderableWidget(button(this.leftPos + 154, this.topPos + 5, 17, 17, 176, 36, 17, CONTAINER_BACKGROUND, button -> {
            this.list.saveChanges(this.menu.getPerson());
            updateButtons();
        }, ConstantComponents.SAVE));
        this.saveButton.active = false;

        this.undoButton = this.addRenderableWidget(button(this.leftPos + 154, this.topPos + 188, 17, 17, 193, 36, 17, CONTAINER_BACKGROUND, button -> {
            this.list.undoChanges();
            updateButtons();
        }, ConstantComponents.UNDO));
        this.undoButton.active = false;
    }

    public void updateButtons() {
        boolean dirty = this.list.hasChanged();
        this.saveButton.active = dirty;
        this.undoButton.active = dirty;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int i, int j, float f) {
        this.renderBackground(graphics);
        super.render(graphics, i, j, f);
        this.renderTooltip(graphics, i, j);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int i, int j) {
        graphics.drawString(
            this.font,
            this.title, this.titleLabelX, this.titleLabelY, 4210752,
            false
        );
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float f, int i, int j) {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        graphics.blit(CONTAINER_BACKGROUND, k, l, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        //Ignore Closing
        if (this.minecraft.options.keyInventory.matches(i, j)) {
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private static ImageButton button(int x, int y, int width, int height, int u, int v, int vOffset, ResourceLocation resourceLocation, Button.OnPress onPress, Component component) {
        ImageButton button = new ImageButton(x, y, width, height, u, v, vOffset, resourceLocation, onPress);
        button.setTooltip(Tooltip.create(component));
        return button;
    }
}