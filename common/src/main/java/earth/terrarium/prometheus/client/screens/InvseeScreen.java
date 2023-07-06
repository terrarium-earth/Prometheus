package earth.terrarium.prometheus.client.screens;

import com.mojang.authlib.GameProfile;
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.menus.InvseeMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class InvseeScreen extends AbstractContainerCursorScreen<InvseeMenu> implements MenuAccess<InvseeMenu> {

    private static final Component YOUR_INVENTORY = Component.translatable("prometheus.invsee.your_inventory");
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/invsee.png");

    private Player renderedPlayer;

    public InvseeScreen(InvseeMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 238;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new ImageButton(this.leftPos + 125, this.topPos + 31, 20, 18, 178, 0, 19, CONTAINER_BACKGROUND, (button) -> {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 100);
            }
        })).setTooltip(Tooltip.create(Component.translatable("prometheus.invsee.enderchest")));
        try {
            this.renderedPlayer = new RemotePlayer(this.minecraft.level, new GameProfile(this.menu.getPlayerUUID(), "Fake Inventory Player"));
        } catch (Exception ignored) {}
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int i, int j, float f) {
        this.renderBackground(graphics);
        super.render(graphics, i, j, f);
        this.renderTooltip(graphics, i, j);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int i, int j) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        graphics.drawString(this.font, YOUR_INVENTORY, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float f, int i, int j) {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        graphics.blit(CONTAINER_BACKGROUND, k, l, 0, 0, this.imageWidth, this.imageHeight);
        if (this.renderedPlayer != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
                k + 87, l + 58,
                20, (float) (k + 51) - i, (float) (l + 75 - 50) - j, this.renderedPlayer);
        }
    }

    public static void open(InvseeMenu menu, Component title) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new InvseeScreen(menu, Minecraft.getInstance().player.getInventory(), title));
        }
    }
}