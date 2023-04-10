package earth.terrarium.prometheus.common.handlers.role;

import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.common.handlers.permission.PermissionHolder;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RoleHandler extends SavedData {

    private static final RoleHandler CLIENT_SIDE = new RoleHandler();

    private final Map<UUID, Set<UUID>> players = new HashMap<>();
    private final RoleMap roles = new RoleMap();

    public RoleHandler() {}

    public RoleHandler(CompoundTag tag) {
        roles.load(tag);
        Set<UUID> identifiers = roles.getIdentifiers();
        CompoundTag players = tag.getCompound("players");
        ModUtils.mapTag(players, UUID::fromString, (key, t) -> t.getList(key, Tag.TAG_STRING))
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

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        Set<UUID> identifiers = this.roles.getIdentifiers();
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
        return tag;
    }

    public static RoleHandler read(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CLIENT_SIDE;
        }
        return read(serverLevel.getServer().overworld().getDataStorage());
    }

    public static RoleHandler read(DimensionDataStorage storage) {
        return storage.computeIfAbsent(RoleHandler::new, RoleHandler::new, "prometheus_roles");
    }

    public static Map<String, TriState> getPermissions(Player player) {
        RoleHandler data = read(player.level);
        List<Role> roles = data.roles.getRoles(data.players.getOrDefault(player.getUUID(), Set.of()));
        Map<String, TriState> permissions = new HashMap<>();
        for (Role role : roles) {
            for (var entry : role.permissions().entrySet()) {
                permissions.compute(entry.getKey(), (key, value) -> value == null || value.isUndefined() ? entry.getValue() : value);
            }
        }
        return permissions;
    }

    public static UUID addRole(Player player, Role role) {
        RoleHandler data = read(player.level);
        UUID id = data.roles.addRole(role);
        data.setDirty();
        return id;
    }

    public static void setRole(Player player, UUID uuid, Role role) {
        RoleHandler data = read(player.level);
        data.roles.setRole(uuid, role);
        data.setDirty();
        updatePlayers(player.getServer());
    }

    public static RoleMap getRoles(Player player) {
        RoleHandler data = read(player.level);
        return data.roles;
    }

    public static void reorder(Player player, List<UUID> newOrder) {
        RoleHandler data = read(player.level);
        data.roles.reorder(newOrder);
        data.setDirty();
        updatePlayers(player.getServer());
    }

    public static Role getHighestRole(Player player) {
        RoleHandler data = read(player.level);
        List<Role> roles = data.roles.getRoles(data.players.getOrDefault(player.getUUID(), Set.of()));
        return roles.get(0);
    }

    public static boolean canModifyRoles(Player player) {
        return player.hasPermissions(2);
    }

    private static void updatePlayers(@Nullable MinecraftServer server) {
        if (server == null) return;
        for (var listPlayer : server.getPlayerList().getPlayers()) {
            if (listPlayer instanceof PermissionHolder holder) {
                holder.prometheus$updatePermissions();
            }
        }
    }
}
