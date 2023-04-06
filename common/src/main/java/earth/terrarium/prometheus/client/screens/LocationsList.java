package earth.terrarium.prometheus.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.utils.CursorScreen;
import earth.terrarium.prometheus.common.menus.LocationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class LocationsList extends SelectionList<LocationsList.Entry> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/location.png");

    private Entry selected;

    public LocationsList(int x, int y, int width, int height, int itemHeight, Consumer<@Nullable Entry> onSelection) {
        super(x, y, width, height, itemHeight, onSelection);
    }

    public void update(Stream<LocationMenu.Location> locations, boolean canDelete) {
        updateEntries(locations.map(location -> new Entry(this, location, canDelete)).toList());
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.selected = entry;
    }

    public static class Entry extends ListEntry {

        private final LocationsList list;
        private final LocationMenu.Location location;
        private final boolean hasDelete;

        public Entry(LocationsList list, LocationMenu.Location location, boolean hasDelete) {
            this.list = list;
            this.location = location;
            this.hasDelete = hasDelete;
        }

        @Override
        protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
            blit(stack, left, top, 0, 211, 160, 20);
            renderButtons(stack, left, top, mouseX, mouseY);

            Minecraft.getInstance().font.drawShadow(stack, Component.literal(location.name()), left + 5, top + 5, 0xFFFFFF);
        }

        private void renderButtons(@NotNull PoseStack stack, int left, int top, int mouseX, int mouseY) {
            if (hasDelete) {
                renderButton(stack, left + 140, top + 2, mouseX, mouseY, "X");
                renderButton(stack, left + 116, top + 2, mouseX, mouseY, ">");
            } else {
                renderButton(stack, left + 140, top + 2, mouseX, mouseY, ">");
            }
        }

        private void renderButton(PoseStack stack, int x, int y, int mouseX, int mouseY, String icon) {
            RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
                blit(stack, x, y, 188, 16, 16, 16);
                if (Minecraft.getInstance().screen instanceof CursorScreen cursorScreen) {
                    cursorScreen.setCursor(CursorScreen.Cursor.POINTER);
                }
            } else {
                blit(stack, x, y, 188, 0, 16, 16);
            }
            Component iconComponent = Component.literal(icon);
            var font = Minecraft.getInstance().font;
            int width = font.width(iconComponent);
            font.drawShadow(stack, iconComponent, x + 1 + (16 - width) / 2f, y + 4f, 0xFFFFFF);
        }

        @Override
        public void setFocused(boolean bl) {}

        @Override
        public boolean isFocused() {
            return list.selected == this;
        }
    }
}
