package earth.terrarium.prometheus.common.menus;

import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.ChangeRolesPacket;
import earth.terrarium.prometheus.common.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RolesMenu extends AbstractContainerMenu {

    private final List<RoleEntry> uneditable;

    private List<RoleEntry> roles;
    private List<RoleEntry> newRoles;

    public RolesMenu(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i, read(buf), buf.readVarInt());
    }
    public RolesMenu(int id, List<RoleEntry> roles, int starting) {
        super(ModMenus.ROLES.get(), id);
        this.uneditable = new ArrayList<>();
        this.roles = new ArrayList<>();
        if (roles != null) {
            for (int i = 0; i < roles.size(); i++) {
                if (i >= starting) {
                    this.roles.add(roles.get(i));
                } else {
                    this.uneditable.add(roles.get(i));
                }
            }
            this.newRoles = new ArrayList<>(this.roles);
        } else {
            this.roles = null;
            this.newRoles = null;
        }
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
        if (i < newRoles.size() && i >= 0 && player instanceof ServerPlayer serverPlayer) {
            RoleEditMenu.open(serverPlayer, newRoles.get(i).id());
            return true;
        }
        return false;
    }

    public boolean hasError() {
        return roles == null;
    }

    public void remove(UUID id) {
        newRoles.removeIf(pair -> pair.id().equals(id));
    }

    public List<RoleEntry> getRoles() {
        return newRoles;
    }

    public void reset() {
        this.newRoles = new ArrayList<>(roles);
    }

    public void save() {
        this.roles = new ArrayList<>(newRoles);
        List<UUID> ids = new ArrayList<>();
        uneditable.forEach(pair -> ids.add(pair.id()));
        roles.forEach(pair -> ids.add(pair.id()));
        NetworkHandler.CHANNEL.sendToServer(new ChangeRolesPacket(ids));
    }

    public int getIndexOf(UUID id) {
        for (int i = 0; i < newRoles.size(); i++) {
            if (newRoles.get(i).id().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public boolean areRolesDifferent() {
        return !newRoles.equals(roles);
    }

    public void move(UUID id, boolean up) {
        int index = -1;
        for (int i = 0; i < newRoles.size(); i++) {
            if (newRoles.get(i).id().equals(id)) {
                index = i;
                break;
            }
        }
        if (index == -1) return;
        if (up) {
            if (index == 0) return;
            RoleEntry role = newRoles.remove(index);
            newRoles.add(index - 1, role);
        } else {
            if (index == newRoles.size() - 1) return;
            RoleEntry role = newRoles.remove(index);
            newRoles.add(index + 1, role);
        }
    }

    public static void write(FriendlyByteBuf buf, List<RoleEntry> roles, int starting) {
        buf.writeCollection(roles, (buffer, entry) -> {
            buffer.writeUUID(entry.id());
            entry.role().toBuffer(buffer);
        });
        buf.writeVarInt(starting);
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
}
