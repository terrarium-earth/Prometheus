package earth.terrarium.prometheus.client.screens.location;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.menus.content.location.Location;
import earth.terrarium.prometheus.common.menus.content.location.LocationContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class LocationScreen extends BaseCursorScreen {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/location.png");
    private static final int HEIGHT = 211;
    private static final int WIDTH = 176;

    private final LocationContent content;

    public LocationScreen(LocationContent content) {
        super(content.type().title());
        this.content = content;
    }

    @Override
    protected void init() {
        super.init();
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;

        ImageButton addButton = this.addRenderableWidget(new ImageButton(leftPos + 157, topPos + 22, 12, 12, 176, 0, 12, CONTAINER_BACKGROUND, (button) ->
            Minecraft.getInstance().setScreen(new AddLocationScreen(this.content.type()))
        ));
        addButton.setTooltip(Tooltip.create(Component.translatable("prometheus.locations." + content.type().getId() + ".add")));
        if (content.locations().size() >= content.max() || !content.canModify()) {
            addButton.active = false;
        }

        LocationsList list = this.addRenderableWidget(new LocationsList(leftPos + 8, topPos + 43, 160, 160, 20, item -> {
            if (item != null) {
                ScreenUtils.sendCommand(this.content.type().tpPrefix() + " " + item.id());
            }
        }));
        list.update(getEntries(content.locations(), ""));

        EditBox searchBar = this.addRenderableWidget(new EditBox(this.font, leftPos + 9, topPos + 24, 143, 11, CommonComponents.EMPTY));
        searchBar.setMaxLength(32);
        searchBar.setBordered(false);
        searchBar.setResponder(text -> list.update(getEntries(content.locations(), text)));
    }

    private static Stream<Location> getEntries(List<Location> locations, String filter) {
        return locations.stream().filter(location -> filter.isBlank() || location.name().toLowerCase().contains(filter.toLowerCase()));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int i, int j, float f) {
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;
        this.renderBackground(graphics);
        graphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, WIDTH, HEIGHT);
        super.render(graphics, i, j, f);
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
}