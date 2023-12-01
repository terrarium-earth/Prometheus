package earth.terrarium.prometheus.client.screens.roles.adding;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.roles.RolesScreen;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.content.MemberRolesContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class MemberRolesScreen extends BaseCursorScreen {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/roles.png");
    private static final int HEIGHT = 212;
    private static final int WIDTH = 176;

    private final MemberRolesContent content;

    private Button saveButton;
    private Button undoButton;
    private MemberRolesList list;

    public MemberRolesScreen(MemberRolesContent content) {
        super(CommonComponents.EMPTY);
        this.content = content;
    }

    public static void open(MemberRolesContent content) {
        Minecraft.getInstance().setScreen(new MemberRolesScreen(content));
    }

    @Override
    protected void init() {
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;

        super.init();

        this.list = this.addRenderableWidget(new MemberRolesList(leftPos + 8, topPos + 24, 144, 180, 20, item -> updateButtons()));
        this.list.update(this.content.roles());

        this.saveButton = this.addRenderableWidget(new ImageButton(leftPos + 154, topPos + 5, 17, 17, RolesScreen.SAVE_BUTTON_SPRITES, button -> {
            this.list.saveChanges(this.content.person());
            updateButtons();
        }));
        this.saveButton.setTooltip(Tooltip.create(ConstantComponents.SAVE));
        this.saveButton.active = false;

        this.undoButton = this.addRenderableWidget(new ImageButton(leftPos + 154, topPos + 188, 17, 17, RolesScreen.UNDO_BUTTON_SPRITES, button -> {
            this.list.undoChanges();
            updateButtons();
        }));
        this.undoButton.setTooltip(Tooltip.create(ConstantComponents.UNDO));
        this.undoButton.active = false;
    }

    public void updateButtons() {
        boolean dirty = this.list.hasChanged();
        this.saveButton.active = dirty;
        this.undoButton.active = dirty;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;
        graphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, WIDTH, HEIGHT, 256, 256);
        graphics.drawString(
            this.font,
            this.title, leftPos + 8, topPos + 6, 4210752,
            false
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}