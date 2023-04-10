package earth.terrarium.prometheus.common.menus;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.prometheus.common.commands.admin.RolesCommand;
import earth.terrarium.prometheus.common.handlers.role.Role;
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
import java.util.UUID;

public class RoleEditMenu extends AbstractContainerMenu {

    private final List<Pair<UUID, Role>> roles;
    private final Role selected;
    private final UUID selectedId;

    public RoleEditMenu(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i, read(buf), buf.readUUID());
    }

    public RoleEditMenu(int id, List<Pair<UUID, Role>> roles, UUID selected) {
        super(ModMenus.ROLE_EDIT.get(), id);
        this.roles = roles;
        this.selected = roles == null ? null : roles.stream()
                .filter(pair -> pair.getFirst().equals(selected))
                .findFirst()
                .map(Pair::getSecond)
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
            open(serverPlayer, roles, i);
            return true;
        }
        return false;
    }

    public List<Pair<UUID, Role>> getRoles() {
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
            if (roles.get(i).getFirst().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public static void write(FriendlyByteBuf buf, List<Pair<UUID, Role>> roles, UUID selected) {
        buf.writeCollection(roles, (buffer, role) -> {
            buffer.writeUUID(role.getFirst());
            role.getSecond().toBuffer(buffer);
        });
        buf.writeUUID(selected);
    }

    public static List<Pair<UUID, Role>> read(FriendlyByteBuf buf) {
        List<Pair<UUID, Role>> roles = buf.readList(buffer -> new Pair<>(buffer.readUUID(), Role.fromBuffer(buffer)));
        for (Pair<UUID, Role> role : roles) {
            if (role.getSecond() == null) {
                return null;
            }
        }
        return roles;
    }

    public static void open(ServerPlayer player, List<Pair<UUID, Role>> roles, int selected) {
        UUID roleId = roles.get(selected).getFirst();
        List<Pair<UUID, Role>> newRoles = RoleHandler.getRoles(player).getRoles();
        ModUtils.openMenu(player,
                (id, inventory, p) -> new RoleEditMenu(id, newRoles, roleId),
                CommonComponents.EMPTY,
                buf -> RoleEditMenu.write(buf, newRoles, roleId)
        );
    }
}
