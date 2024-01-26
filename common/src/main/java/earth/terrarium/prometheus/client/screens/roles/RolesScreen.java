package earth.terrarium.prometheus.client.screens.roles;

import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundAddRolePacket;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundOpenRolePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RolesScreen extends BaseCursorScreen {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/roles.png");
    private static final int HEIGHT = 212;
    private static final int WIDTH = 176;

    private static final WidgetSprites PLUS_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "roles/plus_button"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/plus_button_disabled"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/plus_button_highlighted")
    );

    private static final WidgetSprites MINUS_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "roles/minus_button"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/minus_button_disabled"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/minus_button_highlighted")
    );

    private static final WidgetSprites UP_ARROW_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "roles/up_arrow_button"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/up_arrow_button_disabled"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/up_arrow_button_highlighted")
    );

    private static final WidgetSprites DOWN_ARROW_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "roles/down_arrow_button"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/down_arrow_button_disabled"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/down_arrow_button_highlighted")
    );

    private static final WidgetSprites EDIT_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "roles/edit_button"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/edit_button_disabled"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/edit_button_highlighted")
    );

    public static final WidgetSprites SAVE_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "roles/save_button"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/save_button_disabled"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/save_button_highlighted")
    );

    public static final WidgetSprites UNDO_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "roles/undo_button"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/undo_button_disabled"),
        new ResourceLocation(Prometheus.MOD_ID, "roles/undo_button_highlighted")
    );

    private final RolesContent content;

    private long timeSinceLastSaveWarning = 0;

    private Button saveButton;
    private Button undoButton;
    private Button moveUpButton;
    private Button moveDownButton;
    private Button editButton;
    private Button deleteButton;
    private RolesList list;

    private RolesList.Entry selected;

    public RolesScreen(RolesContent content) {
        super(CommonComponents.EMPTY);
        this.content = content;
    }

    public static void open(RolesContent content) {
        Minecraft.getInstance().setScreen(new RolesScreen(content));
    }

    @Override
    protected void init() {
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;
        if (!this.content.hasError()) {
            this.list = addRenderableWidget(new RolesList(leftPos + 8, topPos + 24, 144, 180, 20, item -> {
                this.selected = item;
                updateButtons();
            }));
            this.list.update(this.content.getRoles());
        }

        this.saveButton = this.addRenderableWidget(button(leftPos + 154, topPos + 5, 17, 17, SAVE_BUTTON_SPRITES, button -> {
            this.content.save();
            updateButtons();
        }, ConstantComponents.SAVE));
        this.saveButton.active = false;

        this.undoButton = this.addRenderableWidget(button(leftPos + 154, topPos + 188, 17, 17, UNDO_BUTTON_SPRITES, button -> {
            this.content.reset();
            this.list.update(this.content.getRoles());
            updateButtons();
        }, ConstantComponents.UNDO));
        this.undoButton.active = false;

        this.addRenderableWidget(button(leftPos + 157, topPos + 27, 12, 12, PLUS_BUTTON_SPRITES, button ->
                NetworkHandler.CHANNEL.sendToServer(new ServerboundAddRolePacket())
            , ConstantComponents.ADD)).active = !this.content.hasError();

        this.deleteButton = this.addRenderableWidget(button(leftPos + 157, topPos + 43, 12, 12, MINUS_BUTTON_SPRITES, button -> {
            if (selected != null) {
                this.content.remove(selected.id());
                this.list.update(this.content.getRoles());
                updateButtons();
            }
        }, ConstantComponents.REMOVE));
        this.deleteButton.active = false;

        this.moveUpButton = this.addRenderableWidget(button(leftPos + 157, topPos + 64, 12, 12, UP_ARROW_BUTTON_SPRITES, button -> {
            if (this.selected != null) {
                this.content.move(this.selected.id(), true);
                this.list.update(this.content.getRoles(), this.selected.id());
                updateButtons();
            }
        }, ConstantComponents.MOVE_UP));
        this.moveUpButton.active = false;

        this.moveDownButton = this.addRenderableWidget(button(leftPos + 157, topPos + 79, 12, 12, DOWN_ARROW_BUTTON_SPRITES, button -> {
            if (this.selected != null) {
                this.content.move(this.selected.id(), false);
                this.list.update(this.content.getRoles(), this.selected.id());
                updateButtons();
            }
        }, ConstantComponents.MOVE_DOWN));
        this.moveDownButton.active = false;

        this.editButton = this.addRenderableWidget(button(leftPos + 157, topPos + 99, 12, 12, EDIT_BUTTON_SPRITES, button -> {
            if (this.saveButton.active) {
                this.timeSinceLastSaveWarning = System.currentTimeMillis();
            } else if (Minecraft.getInstance().gameMode != null && this.selected != null) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenRolePacket(this.selected.id()));
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
        this.saveButton.active = this.undoButton.active = this.content.areRolesDifferent();
        this.deleteButton.active = this.selected != null && selectedIndex < this.content.getRoles().size() - 1;
        this.moveUpButton.active = this.selected != null && selectedIndex > 0 && selectedIndex < this.content.getRoles().size() - 1;
        this.moveDownButton.active = this.selected != null && selectedIndex < this.content.getRoles().size() - 2;
        this.editButton.active = this.selected != null;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;
        graphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, WIDTH, HEIGHT);
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(leftPos, topPos, 0);
            Component title = this.saveButton.active && System.currentTimeMillis() - this.timeSinceLastSaveWarning < 2500 ? ConstantComponents.UNSAVED_CHANGES : this.title;
            graphics.drawString(this.font, title, 8, 8, 0x404040, false);

        }
        if (this.content.hasError()) {
            graphics.fill(0, 0, this.width, this.height, 0x88000000);
            graphics.drawCenteredString(this.font, ConstantComponents.ERROR_IN_LOGS, this.width / 2, this.height / 2, 0xFF0000);
        }
    }

    private static ImageButton button(int x, int y, int width, int height, WidgetSprites sprites, Button.OnPress onPress, Component component) {
        ImageButton button = new ImageButton(x, y, width, height, sprites, onPress);
        button.setTooltip(Tooltip.create(component));
        return button;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}