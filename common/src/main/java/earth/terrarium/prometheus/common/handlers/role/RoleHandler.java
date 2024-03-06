package earth.terrarium.prometheus.common.handlers.role;

import com.teamresourceful.resourcefullib.common.nbt.TagUtils;
import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.prometheus.api.events.MemberRolesChangedEvent;
import earth.terrarium.prometheus.api.events.ServerRolesUpdatedEvent;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.handlers.permission.PermissionHolder;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RoleHandler extends SaveHandler {

    private static final RoleHandler CLIENT_SIDE = new RoleHandler();

    private final Map<UUID, Set<UUID>> players = new HashMap<>();
    private final RoleMap roles = new RoleMap();

    public static RoleHandler read(Level level) {
        return read(level, HandlerType.create(CLIENT_SIDE, RoleHandler::new), "prometheus_roles");
    }

    public static Map<String, TriState> getOfflinePermissions(Level level, UUID uuid) {
        RoleHandler data = read(level);
        List<RoleEntry> roles = data.roles.roles(data.players.getOrDefault(uuid, Set.of()));
        Map<String, TriState> permissions = new HashMap<>();
        for (RoleEntry entry : roles) {
            entry.role().permissions().forEach((key, value) -> permissions.compute(key, (k, v) -> TriState.map(v, value)));
        }
        return permissions;
    }

    public static Map<String, TriState> getPermissions(Player player) {
        return getOfflinePermissions(player.level(), player.getUUID());
    }

    public static void changeRoles(Level level, UUID target, Object2BooleanMap<UUID> roles) {
        handle(level, RoleHandler::read, data ->
            data.players.compute(target, (key, value) -> {
                final Set<UUID> map = value == null ? new HashSet<>() : new HashSet<>(value);
                roles.forEach((id, has) -> {
                    if (has) {
                        map.add(id);
                    } else {
                        map.remove(id);
                    }
                });
                return map;
            })
        );
        ServerPlayer serverPlayer = level.getServer() == null ? null : level.getServer().getPlayerList().getPlayer(target);
        if (serverPlayer != null) {
            if (serverPlayer instanceof PermissionHolder holder) {
                holder.prometheus$updatePermissions();
            }
            if (serverPlayer instanceof RoleEntityHook hook) {
                hook.prometheus$updateHighestRole();
            }
        }
        MemberRolesChangedEvent.fire(new MemberRolesChangedEvent(level.getServer(), target));
    }

    public static void setRole(Player player, UUID uuid, Role role) {
        handle(player.level(), RoleHandler::read, data -> data.roles.set(uuid, role));
        if (uuid != null) {
            updatePlayers(player.getServer());
        }
        ServerRolesUpdatedEvent.fire(new ServerRolesUpdatedEvent(player.getServer()));
    }

    public static RoleMap roles(Level level) {
        return read(level).roles;
    }

    public static void reorder(Player player, List<UUID> newOrder) {
        RoleHandler data = read(player.level());
        data.roles.reorder(newOrder);
        data.setDirty();
        updatePlayers(player.getServer());
        ServerRolesUpdatedEvent.fire(new ServerRolesUpdatedEvent(player.getServer()));
    }

    public static Set<UUID> getEditableRoles(Player player) {
        RoleHandler data = read(player.level());
        if (player.getServer() != null && player.getServer().getPlayerList().isOp(player.getGameProfile())) {
            Set<UUID> roles = new HashSet<>(data.roles.ids());
            roles.add(DefaultRole.DEFAULT_ROLE);
            return roles;
        }
        Set<UUID> roles = data.players.getOrDefault(player.getUUID(), Set.of());
        Set<UUID> editable = new HashSet<>();
        boolean canModify = false;
        for (RoleEntry role : data.roles) {
            if (canModify) {
                editable.add(role.id());
            }
            if (roles.contains(role.id())) {
                canModify = true;
            }
        }
        editable.add(DefaultRole.DEFAULT_ROLE);
        return editable;
    }

    public static boolean canModifyRole(Player player, UUID id) {
        return getEditableRoles(player).contains(id);
    }

    public static Role getHighestRole(Player player) {
        return getHighestRole(player.level(), player.getUUID());
    }

    public static Role getHighestRole(Level level, UUID player) {
        RoleHandler data = read(level);
        List<RoleEntry> roles = data.roles.roles(data.players.getOrDefault(player, Set.of()));
        return roles.get(0).role();
    }

    public static Set<UUID> getRolesForPlayer(Player player, UUID id) {
        RoleHandler data = read(player.level());
        return data.players.getOrDefault(id, Set.of());
    }

    public static boolean canModifyRoles(Player player) {
        return PermissionApi.API.getPermission(player, "roles.manage").map(player.hasPermissions(2));
    }

    private static void updatePlayers(@Nullable MinecraftServer server) {
        if (server == null) return;
        for (var listPlayer : server.getPlayerList().getPlayers()) {
            if (listPlayer instanceof RoleEntityHook holder) {
                holder.prometheus$updateHighestRole();
            }
            if (listPlayer instanceof PermissionHolder holder) {
                holder.prometheus$updatePermissions();
            }
        }
    }

    @Override
    public void saveData(@NotNull CompoundTag tag) {
        Set<UUID> identifiers = this.roles.ids();
        CompoundTag players = new CompoundTag();
        this.players.forEach((player, ids) -> {
            ListTag roles = new ListTag();
            for (UUID id : ids) {
                if (identifiers.contains(id)) {
                    roles.add(StringTag.valueOf(id.toString()));
                }
            }
            players.put(player.toString(), roles);
        });
        tag.put("players", players);
        this.roles.save(tag);
    }

    @Override
    public void loadData(CompoundTag tag) {
        roles.load(tag);
        Set<UUID> identifiers = roles.ids();
        TagUtils.mapTag(tag.getCompound("players"), UUID::fromString, (key, t) -> t.getList(key, Tag.TAG_STRING))
            .forEach((player, roles) -> {
                Set<UUID> roleIds = new HashSet<>();
                for (Tag role : roles) {
                    UUID id = UUID.fromString(role.getAsString());
                    if (identifiers.contains(id)) {
                        roleIds.add(id);
                    }
                }
                this.players.put(player, roleIds);
            });
    }
}
