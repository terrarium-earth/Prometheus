package earth.terrarium.prometheus.common.menus;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.RemoveRolePacket;
import earth.terrarium.prometheus.common.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RolesMenu extends AbstractContainerMenu {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final List<UUID> removed = new ArrayList<>();
    private List<Pair<UUID, Role>> roles;
    private List<Pair<UUID, Role>> newRoles;


    public RolesMenu(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i, read(buf));
    }

    public RolesMenu(int id, List<Pair<UUID, Role>> roles) {
        super(ModMenus.ROLES.get(), id);
        this.roles = roles;
        this.newRoles = new ArrayList<>(roles);
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
        return false;
    }

    public boolean hasError() {
        return roles == null;
    }

    public void remove(UUID id) {
        newRoles.removeIf(pair -> pair.getFirst().equals(id));
        removed.add(id);
    }

    public List<Pair<UUID, Role>> getRoles() {
        return newRoles;
    }

    public void reset() {
        this.newRoles = new ArrayList<>(roles);
        this.removed.clear();
    }

    public void save() {
        if (!this.removed.isEmpty()) {
            NetworkHandler.CHANNEL.sendToServer(new RemoveRolePacket(this.removed));
        }
        this.roles = new ArrayList<>(newRoles);
        //TODO send packet to reset
    }

    public boolean areRolesDifferent() {
        return !newRoles.equals(roles);
    }

    public void move(UUID id, boolean up) {
        int index = -1;
        for (int i = 0; i < newRoles.size(); i++) {
            if (newRoles.get(i).getFirst().equals(id)) {
                index = i;
                break;
            }
        }
        if (index == -1) return;
        if (up) {
            if (index == 0) return;
            Pair<UUID, Role> role = newRoles.remove(index);
            newRoles.add(index - 1, role);
        } else {
            if (index == newRoles.size() - 1) return;
            Pair<UUID, Role> role = newRoles.remove(index);
            newRoles.add(index + 1, role);
        }
    }

    public static void write(FriendlyByteBuf buf, List<Pair<UUID, Role>> roles){
        buf.writeCollection(roles, (buffer, role) -> {
            PacketHelper.writeWithYabn(buffer, Role.CODEC, role.getSecond(), true);
            buffer.writeUUID(role.getFirst());
        });
    }

    public static List<Pair<UUID, Role>> read(FriendlyByteBuf buf) {
        List<Pair<UUID, Role>> roles = buf.readList(buffer -> {
            Role role = PacketHelper.readWithYabn(buffer, Role.CODEC, true).get()
                    .ifRight(error -> LOGGER.error("Error reading role: {}", error))
                    .left()
                    .orElse(null);
            return role == null ? null : new Pair<>(buffer.readUUID(), role);
        });
        for (Pair<UUID, Role> role : roles) {
            if (role == null) {
                return null;
            }
        }
        return roles;
    }
}
