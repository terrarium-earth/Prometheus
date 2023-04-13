package earth.terrarium.prometheus.client.screens.location;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.locations.client.LocationDisplayApi;
import earth.terrarium.prometheus.client.utils.ClientUtils;
import earth.terrarium.prometheus.common.menus.location.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class LocationsList extends SelectionList<LocationsList.Entry> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/location.png");
    private static final ResourceLocation DEFAULT_ICON = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_unknown.png");

    private Entry selected;

    public LocationsList(int x, int y, int width, int height, int itemHeight, Consumer<@Nullable Entry> onSelection) {
        super(x, y, width, height, itemHeight, onSelection);
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
        protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
            blit(stack, left, top, 0, hovered ? 231 : 211, 160, 20);
            RenderSystem.setShaderTexture(0, icon);
            blit(stack, left + 5, top + 2, 0, 0, 16, 16, 16, 16);
            Minecraft.getInstance().font.drawShadow(stack, Component.literal(location.name()), left + 25, top + 5, 0xFFFFFF);
            CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
            if (mouseX >= left + 5 && mouseX <= left + 21 && mouseY >= top + 2 && mouseY <= top + 18) {
                ClientUtils.setTooltip(Component.translatableWithFallback(
                        location.pos().dimension().location().toLanguageKey("dimension"),
                        location.pos().dimension().location().toString()
                ));
            }
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
