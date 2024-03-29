package earth.terrarium.prometheus.client.screens.location;

import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.base.PriorityScreen;
import earth.terrarium.prometheus.client.screens.widgets.ContextMenu;
import earth.terrarium.prometheus.client.screens.widgets.ContextualMenuScreen;
import earth.terrarium.prometheus.common.menus.content.location.Location;
import earth.terrarium.prometheus.common.menus.content.location.LocationContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Stream;

public class LocationScreen extends PriorityScreen implements ContextualMenuScreen {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/location.png");
    private static final int HEIGHT = 211;
    private static final int WIDTH = 176;

    private static final WidgetSprites PLUS_BUTTON_SPRITES = new WidgetSprites(
        new ResourceLocation(Prometheus.MOD_ID, "location/plus_button"),
        new ResourceLocation(Prometheus.MOD_ID, "location/plus_button_disabled"),
        new ResourceLocation(Prometheus.MOD_ID, "location/plus_button_highlighted")
    );

    private final LocationContent content;

    private ContextMenu contextMenu;

    public LocationScreen(LocationContent content) {
        super(content.type().title());
        this.content = content;
    }

    public static void open(LocationContent content) {
        Minecraft.getInstance().setScreen(new LocationScreen(content));
    }

    @Override
    protected void init() {
        super.init();
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;

        ImageButton addButton = this.addRenderableWidget(new ImageButton(leftPos + 157, topPos + 22, 12, 12, PLUS_BUTTON_SPRITES, (button) ->
            Minecraft.getInstance().setScreen(new AddLocationScreen(this, this.content.type()))
        ));
        addButton.setTooltip(Tooltip.create(Component.translatable("prometheus.locations." + content.type().getId() + ".add")));
        if (content.locations().size() >= content.max() || !content.canModify()) {
            addButton.active = false;
        }

        LocationsList list = this.addRenderableWidget(new LocationsList(leftPos + 8, topPos + 43, 160, 160, 20,
            this.content, item -> {
            if (item != null) {
                ScreenUtils.sendCommand(this.content.type().tpPrefix() + " " + item.id());
                onClose();
            }
        }));
        list.update(getEntries(content.locations(), ""));

        EditBox searchBar = this.addRenderableWidget(new EditBox(this.font, leftPos + 9, topPos + 24, 143, 11, CommonComponents.EMPTY));
        searchBar.setMaxLength(32);
        searchBar.setBordered(false);
        searchBar.setResponder(text -> list.update(getEntries(content.locations(), text)));

        this.contextMenu = addRenderableWidget(-1, new ContextMenu());
    }

    private static Stream<Location> getEntries(List<Location> locations, String filter) {
        return locations.stream().filter(location -> filter.isBlank() || location.name().toLowerCase().contains(filter.toLowerCase()));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;
        graphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, WIDTH, HEIGHT);
        graphics.drawString(
            this.font,
            title, leftPos + 8, topPos + 6, 4210752,
            false
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public ContextMenu getContextMenu() {
        return this.contextMenu;
    }
}