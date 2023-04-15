package earth.terrarium.prometheus.client.screens.roles.editing;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.utils.ClientUtils;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class QuickEditList extends SelectionList<QuickEditList.Entry> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/edit_role.png");

    private Entry selected;

    public QuickEditList(int x, int y, int width, int height, int itemHeight, Consumer<@Nullable Entry> onSelection) {
        super(x, y, width, height, itemHeight, onSelection);
    }

    public void update(List<RoleEntry> roles) {
        updateEntries(List.of());
        for (var role : roles) {
            addEntry(new Entry(role.id(), role.role()));
        }
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
        protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
            blit(stack, left, top, 300, hovered ? 20 : 0, 42, 20, 512, 512);

            Gui.drawCenteredString(stack, Minecraft.getInstance().font, String.valueOf(display.icon()), left + 21, top + 5, 0xFFFFFF);
            if (hovered) {
                CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
                ClientUtils.setTooltip(Component.literal(display.display()));
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
