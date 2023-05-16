package earth.terrarium.prometheus.client.screens.location;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.menus.location.Location;
import earth.terrarium.prometheus.common.menus.location.LocationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class LocationScreen extends AbstractContainerCursorScreen<LocationMenu> implements MenuAccess<LocationMenu> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/location.png");

    public LocationScreen(LocationMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.passEvents = false;
        this.imageHeight = 211;
        this.imageWidth = 176;
    }

    @Override
    protected void init() {
        super.init();
        ImageButton addButton = this.addRenderableWidget(new ImageButton(this.leftPos + 157, this.topPos + 22, 12, 12, 176, 0, 12, CONTAINER_BACKGROUND, (button) ->
            Minecraft.getInstance().setScreen(new AddLocationScreen(this.menu.getLocationType()))
        ));
        addButton.setTooltip(Tooltip.create(Component.translatable("prometheus.locations." + menu.getLocationType().getId() + ".add")));
        if (menu.getLocations().size() >= menu.getMax() || !menu.canModify()) {
            addButton.active = false;
        }

        LocationsList list = this.addRenderableWidget(new LocationsList(this.leftPos + 8, this.topPos + 43, 160, 160, 20, item -> {
            if (item != null) {
                sendClick(item.id());
            }
        }));
        list.update(getEntries(menu.getLocations(), ""));

        EditBox searchBar = this.addRenderableWidget(new EditBox(this.font, this.leftPos + 9, this.topPos + 24, 143, 11, CommonComponents.EMPTY));
        searchBar.setMaxLength(32);
        searchBar.setBordered(false);
        searchBar.setResponder(text -> list.update(getEntries(menu.getLocations(), text)));
    }

    private static Stream<Location> getEntries(List<Location> locations, String filter) {
        return locations.stream().filter(location -> filter.isBlank() || location.name().toLowerCase().contains(filter.toLowerCase()));
    }

    @Override
    public void render(@NotNull PoseStack stack, int i, int j, float f) {
        this.renderBackground(stack);
        super.render(stack, i, j, f);
        this.renderTooltip(stack, i, j);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack stack, int i, int j) {
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

    public void sendClick(String name) {
        if (this.minecraft.gameMode == null) return;
        for (int i = 0; i < this.menu.getLocations().size(); i++) {
            if (this.menu.getLocations().get(i).name().equals(name)) {
                this.minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
                break;
            }
        }
    }
}