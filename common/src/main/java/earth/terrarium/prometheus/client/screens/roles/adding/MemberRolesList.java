package earth.terrarium.prometheus.client.screens.roles.adding;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.menus.MemberRolesMenu;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.MemberRolesPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MemberRolesList extends SelectionList<MemberRolesList.Entry> {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/list_buttons.png");
    private static final ResourceLocation BUTTONS = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/buttons.png");

    public MemberRolesList(int x, int y, int width, int height, int itemHeight, Consumer<MemberRolesList.Entry> onSelection) {
        super(x, y, width, height, itemHeight, onSelection, true);
    }

    public void update(List<MemberRolesMenu.MemberRole> roles) {
        updateEntries(List.of());
        for (var role : roles) {
            addEntry(new Entry(role.id(), role.name(), role.selected()));
        }
    }

    public boolean hasChanged() {
        for (Entry child : this.children()) {
            if (child.selected != child.original) {
                return true;
            }
        }
        return false;
    }

    public void undoChanges() {
        for (Entry child : this.children()) {
            child.selected = child.original;
        }
    }

    public void saveChanges(UUID target) {
        Object2BooleanMap<UUID> changes = new Object2BooleanOpenHashMap<>();
        for (Entry child : this.children()) {
            changes.put(child.id, child.selected);
            child.original = child.selected;
        }
        NetworkHandler.CHANNEL.sendToServer(new MemberRolesPacket(target, changes));

    }

    public class Entry extends ListEntry {

        private final UUID id;
        private final String display;
        private boolean selected;
        private boolean original;

        public Entry(UUID id, String display, boolean original) {
            this.id = id;
            this.display = display;
            this.original = original;
            this.selected = original;
        }

        @Override
        protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack scissor, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
            graphics.blit(CONTAINER_BACKGROUND, left, top, 0, 0, 144, 20);

            graphics.drawString(
                Minecraft.getInstance().font,
                this.display, left + 5, top + 5, 0xFFFFFF,
                false
            );


            RenderSystem.setShaderTexture(0, BUTTONS);
            graphics.blit(CONTAINER_BACKGROUND, left + width - 33 - 4, top + 4, 0, 36, 23, 12);
            if (this.selected) {
                graphics.blit(CONTAINER_BACKGROUND, left + width - 26, top + 4, 24, 12, 12, 12);
            } else {
                graphics.blit(CONTAINER_BACKGROUND, left + width - 33 - 4, top + 4, 0, 12, 12, 12);
            }
            if (hovered && mouseY >= top + 4 && mouseY <= top + 4 + 12) {
                if (mouseX >= left + width - 33 - 4 && mouseX < left + width - 33 - 4 + 11) {
                    graphics.blit(CONTAINER_BACKGROUND, left + width - 33 - 4, top + 4, 0, 0, 12, 12);
                    CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
                } else if (mouseX >= left + width - 26 && mouseX < left + width - 26 + 11) {
                    graphics.blit(CONTAINER_BACKGROUND, left + width - 26, top + 4, 24, 0, 12, 12);
                    CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
                }
            }
        }

        @Override
        public boolean mouseClicked(double x, double y, int i) {
            if (i == InputConstants.MOUSE_BUTTON_LEFT && y >= 4 && y <= 4 + 12) {
                if (x >= 107 && x <= 130) {
                    selected = !selected;
                    return true;
                }
            }
            return super.mouseClicked(x, y, i);
        }

        public UUID id() {
            return id;
        }

        @Override
        public void setFocused(boolean bl) {}

        @Override
        public boolean isFocused() {
            return this == getSelected();
        }
    }
}
