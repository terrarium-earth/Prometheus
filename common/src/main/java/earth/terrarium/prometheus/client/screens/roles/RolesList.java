package earth.terrarium.prometheus.client.screens.roles;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class RolesList extends SelectionList<RolesList.Entry> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/list_buttons.png");

    private Entry selected;

    public RolesList(int x, int y, int width, int height, int itemHeight, Consumer<@Nullable Entry> onSelection) {
        super(x, y, width, height, itemHeight, onSelection);
    }

    public void update(List<RoleEntry> roles) {
        update(roles, null);
    }

    public void update(List<RoleEntry> roles, UUID selected) {
        updateEntries(List.of());
        Entry selectedEntry = null;
        for (var role : roles) {
            Entry entry = new Entry(role.id(), role.role());
            if (role.id().equals(selected)) {
                selectedEntry = entry;
            }
            addEntry(entry);
        }
        setSelected(selectedEntry);
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.selected = entry;
    }

    public class Entry extends ListEntry {

        private final UUID id;
        private final CosmeticOptions display;

        public Entry(UUID id, Role role) {
            this.id = id;
            this.display = role.getOption(CosmeticOptions.SERIALIZER);
        }

        @Override
        protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissor, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            int offset = selected ? 40 : 0;
            graphics.blit(CONTAINER_BACKGROUND, left, top, 0, hovered ? offset + 20 : offset, 144, 20);

            graphics.drawString(
                Minecraft.getInstance().font,
                this.display.display(), left + 5, top + 5, 0xFFFFFF,
                false
            );
            if (Minecraft.getInstance().screen instanceof CursorScreen cursorScreen && hovered) {
                cursorScreen.setCursor(CursorScreen.Cursor.POINTER);
            }
        }

        public UUID id() {
            return id;
        }

        @Override
        public void setFocused(boolean bl) {}

        @Override
        public boolean isFocused() {
            return this == selected;
        }
    }
}
