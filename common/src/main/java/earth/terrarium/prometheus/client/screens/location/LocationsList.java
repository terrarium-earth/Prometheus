package earth.terrarium.prometheus.client.screens.location;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.locations.client.LocationDisplayApi;
import earth.terrarium.prometheus.client.screens.widgets.ContextualMenuScreen;
import earth.terrarium.prometheus.common.menus.content.location.Location;
import earth.terrarium.prometheus.common.menus.content.location.LocationContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.DeleteLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class LocationsList extends SelectionList<LocationsList.Entry> {

    private static final ResourceLocation ENTRY = new ResourceLocation(Prometheus.MOD_ID, "location/entry");
    private static final ResourceLocation ENTRY_HIGHLIGHTED = new ResourceLocation(Prometheus.MOD_ID, "location/entry_highlighted");
    private static final ResourceLocation DEFAULT_ICON = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_unknown.png");

    private Entry selected;
    private final Consumer<@Nullable Entry> onSelection;
    private final LocationContent content;

    public LocationsList(int x, int y, int width, int height, int itemHeight, LocationContent content, Consumer<@Nullable Entry> onSelection) {
        super(x, y, width, height, itemHeight, entry -> {});
        this.onSelection = onSelection;
        this.content = content;
    }

    public void update(Stream<Location> locations) {
        updateEntries(locations.map(location -> new Entry(this, location)).toList());
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.selected = entry;
    }

    public static class Entry extends ListEntry {

        private final LocationsList list;
        private final Location location;
        private final ResourceLocation icon;

        public Entry(LocationsList list, Location location) {
            this.list = list;
            this.location = location;
            var locIcon = LocationDisplayApi.API.getIcon(location.pos().dimension());
            this.icon = locIcon == null ? DEFAULT_ICON : locIcon;
        }

        @Override
        protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissor, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            graphics.blitSprite(hovered ? ENTRY_HIGHLIGHTED : ENTRY, left, top, 160, 20);
            graphics.blit(icon, left + 5, top + 2, 0, 0, 16, 16, 16, 16);
            graphics.drawString(
                Minecraft.getInstance().font,
                Component.literal(location.name()), left + 25, top + 5, 0xFFFFFF,
                false
            );
            CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
            if (mouseX >= left + 5 && mouseX <= left + 21 && mouseY >= top + 2 && mouseY <= top + 18) {
                ScreenUtils.setTooltip(Component.translatableWithFallback(
                    location.pos().dimension().location().toLanguageKey("dimension"),
                    location.pos().dimension().location().toString()
                ));
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == InputConstants.MOUSE_BUTTON_LEFT) {
                list.onSelection.accept(this);
                return true;
            }
            if (button == InputConstants.MOUSE_BUTTON_RIGHT && list.content.canModify()) {
                ContextualMenuScreen.getMenu()
                    .ifPresent(menu -> menu.start(mouseX, mouseY)
                        .addOption(Component.literal("Delete"), () ->
                            NetworkHandler.CHANNEL.sendToServer(new DeleteLocationPacket(list.content.type(), location.name()))
                        )
                        .open());
                return true;
            }
            return false;
        }

        @Override
        public void setFocused(boolean bl) {}

        @Override
        public boolean isFocused() {
            return list.selected == this;
        }

        public String id() {
            return location.name();
        }
    }
}
