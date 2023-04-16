package earth.terrarium.prometheus.common.menus;

import earth.terrarium.prometheus.common.commands.admin.RolesCommand;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.registries.ModMenus;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RoleEditMenu extends AbstractContainerMenu {

    private final List<RoleEntry> roles;
    private final Role selected;
    private final UUID selectedId;

    public RoleEditMenu(int i, Inventory ignored, FriendlyByteBuf buf) {
        this(i, read(buf), buf.readUUID());
    }

    public RoleEditMenu(int id, List<RoleEntry> roles, UUID selected) {
        super(ModMenus.ROLE_EDIT.get(), id);
        this.roles = roles;
        this.selected = roles == null ? null : roles.stream()
            .filter(entry -> entry.id().equals(selected))
            .findFirst()
            .map(RoleEntry::role)
            .orElse(null);
        this.selectedId = selected;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int i) {
        if (i == -10 && player instanceof ServerPlayer serverPlayer) {
            RolesCommand.openRolesMenu(serverPlayer);
            return true;
        }
        if (i < roles.size() && i >= 0 && player instanceof ServerPlayer serverPlayer) {
            open(serverPlayer, roles.get(i).id());
            return true;
        }
        return false;
    }

    public List<RoleEntry> getRoles() {
        return roles;
    }

    public Role getSelected() {
        return selected;
    }

    public UUID getSelectedId() {
        return selectedId;
    }

    public int getIndexOf(UUID id) {
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).id().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public static void write(FriendlyByteBuf buf, List<RoleEntry> roles, UUID selected) {
        buf.writeCollection(roles, (buffer, role) -> {
            buffer.writeUUID(role.id());
            role.role().toBuffer(buffer);
        });
        buf.writeUUID(selected);
    }

    public static List<RoleEntry> read(FriendlyByteBuf buf) {
        List<RoleEntry> roles = buf.readList(buffer -> new RoleEntry(buffer.readUUID(), Role.fromBuffer(buffer)));
        for (RoleEntry role : roles) {
            if (role.role() == null) {
                return null;
            }
        }
        return roles;
    }

    public static void open(ServerPlayer player, UUID selected) {
        Set<UUID> editable = RoleHandler.getEditableRoles(player);
        if (!editable.contains(selected)) {
            player.sendSystemMessage(ConstantComponents.CANT_EDIT_ROLE);
            return;
        }
        List<RoleEntry> roles = RoleHandler.roles(player).roles().stream()
            .filter(entry -> editable.contains(entry.id()))
            .toList();
        ModUtils.openMenu(player,
            (id, inventory, p) -> new RoleEditMenu(id, roles, selected),
            CommonComponents.EMPTY,
            buf -> RoleEditMenu.write(buf, roles, selected)
        );
    }
}
