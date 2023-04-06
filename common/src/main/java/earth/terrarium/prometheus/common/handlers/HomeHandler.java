package earth.terrarium.prometheus.common.handlers;

import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HomeHandler extends SavedData {

    public static final int MAX_HOMES = 5;

    private static final HomeHandler CLIENT_SIDE = new HomeHandler();

    private final Map<UUID, Map<String, GlobalPos>> homes = new HashMap<>();

    public HomeHandler() {}

    public HomeHandler(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> {
            CompoundTag homeTag = tag.getCompound(key);
            Map<String, GlobalPos> homeMap = new HashMap<>();
            homeTag.getAllKeys().forEach(homeKey -> homeMap.put(homeKey, ModUtils.fromTag(homeTag.getCompound(homeKey))));
            homes.put(UUID.fromString(key), homeMap);
        });
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        homes.forEach((key, value) -> {
            CompoundTag homeTag = new CompoundTag();
            value.forEach((name, pos) -> homeTag.put(name, ModUtils.toTag(pos)));
            tag.put(key.toString(), homeTag);
        });
        return tag;
    }

    public static HomeHandler read(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CLIENT_SIDE;
        }
        return read(serverLevel.getServer().overworld().getDataStorage());
    }

    public static HomeHandler read(DimensionDataStorage storage) {
        return storage.computeIfAbsent(HomeHandler::new, HomeHandler::new, "prometheus_homes");
    }

    public static void add(Player player, String name) {
        HomeHandler data = read(player.level);
        Map<String, GlobalPos> homes = data.homes.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>());
        if (homes.size() >= MAX_HOMES) {
            player.sendSystemMessage(Component.literal("You have reached the maximum number of homes"));
            return;
        } else if (homes.containsKey(name)) {
            player.sendSystemMessage(Component.literal("Home already exists"));
            return;
        }
        homes.put(name, GlobalPos.of(player.level.dimension(), player.blockPosition()));
        data.setDirty();
    }

    public static void remove(Player player, String name) {
        HomeHandler data = read(player.level);
        Map<String, GlobalPos> homes = data.homes.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>());
        if (!homes.containsKey(name)) {
            player.sendSystemMessage(Component.literal("Home does not exist"));
            return;
        }
        homes.remove(name);
        data.setDirty();
    }

    public static void teleport(Player player, String name) {
        HomeHandler data = read(player.level);
        if (data.homes.get(player.getUUID()) == null) {
            player.sendSystemMessage(Component.literal("You have no homes"));
            return;
        }
        GlobalPos pos = data.homes.get(player.getUUID()).get(name);
        if (pos == null) {
            player.sendSystemMessage(Component.literal("Home does not exist"));
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            ServerLevel level = serverPlayer.server.getLevel(pos.dimension());
            if (level == null) {
                player.sendSystemMessage(Component.literal("Dimension not found"));
                return;
            }

            serverPlayer.teleportTo(level, pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), player.getYRot(), player.getXRot());
        }
    }

    public static Collection<String> getHomes(Player player) {
        HomeHandler data = read(player.level);
        if (data.homes.get(player.getUUID()) == null) {
            return List.of();
        }
        return data.homes.get(player.getUUID()).keySet();
    }

    public static Map<String, GlobalPos> getHomesMap(Player player) {
        HomeHandler data = read(player.level);
        if (data.homes.get(player.getUUID()) == null) {
            return Map.of();
        }
        return data.homes.get(player.getUUID());
    }
}
