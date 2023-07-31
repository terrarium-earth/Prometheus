package earth.terrarium.prometheus.common.handlers;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeHandler extends SaveHandler {

    private static final HomeHandler CLIENT_SIDE = new HomeHandler();

    private final Map<UUID, Map<String, GlobalPos>> homes = new HashMap<>();

    public static HomeHandler read(Level level) {
        return read(level, CLIENT_SIDE, HomeHandler::new, "prometheus_homes");
    }

    public static boolean add(ServerPlayer player, String name) {
        Map<String, GlobalPos> homes = getHomes(player.level()).computeIfAbsent(player.getUUID(), uuid -> new HashMap<>());
        if (homes.size() >= RoleApi.API.getNonNullOption(player, HomeOptions.SERIALIZER).max()) {
            player.sendSystemMessage(ConstantComponents.MAX_HOMES);
            return false;
        } else if (homes.containsKey(name)) {
            player.sendSystemMessage(ConstantComponents.HOME_ALREADY_EXISTS);
            return false;
        }
        homes.put(name, GlobalPos.of(player.level().dimension(), player.blockPosition()));
        read(player.level()).setDirty();
        return true;
    }

    public static void remove(ServerPlayer player, String name) {
        Map<String, GlobalPos> homes = getHomes(player);
        if (!homes.containsKey(name)) {
            player.sendSystemMessage(ConstantComponents.HOME_DOES_NOT_EXIST);
            return;
        }
        homes.remove(name);
        read(player.level()).setDirty();
    }

    public static void teleport(ServerPlayer player, String name) {
        Map<String, GlobalPos> homes = getHomes(player);
        if (homes.isEmpty()) {
            player.sendSystemMessage(ConstantComponents.NO_HOMES);
            return;
        }
        if (!homes.containsKey(name)) {
            player.sendSystemMessage(ConstantComponents.HOME_DOES_NOT_EXIST);
            return;
        }
        GlobalPos pos = homes.get(name);
        ServerLevel level = player.server.getLevel(pos.dimension());
        if (level == null) {
            player.sendSystemMessage(ConstantComponents.NO_DIMENSION);
            return;
        }

        ModUtils.teleport(player, level, pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), player.getYRot(), player.getXRot());
    }

    public static boolean teleport(ServerPlayer player) {
        Map<String, GlobalPos> homes = getHomes(player);
        if (homes.size() == 1) {
            teleport(player, homes.keySet().iterator().next());
            return true;
        }
        if (homes.isEmpty()) {
            player.sendSystemMessage(ConstantComponents.NO_HOMES);
            return true;
        }
        for (String home : homes.keySet()) {
            if (home.equalsIgnoreCase("home") || home.equalsIgnoreCase("bed")) {
                teleport(player, home);
                return true;
            }
        }
        return false;
    }

    public static Map<String, GlobalPos> getHomes(Player player) {
        return getHomes(player.level()).getOrDefault(player.getUUID(), Map.of());
    }

    public static Map<UUID, Map<String, GlobalPos>> getHomes(Level level) {
        return read(level).homes;
    }

    @Override
    public void saveData(@NotNull CompoundTag tag) {
        homes.forEach((key, value) -> {
            CompoundTag homeTag = new CompoundTag();
            value.forEach((name, pos) -> homeTag.put(name, ModUtils.toTag(pos)));
            tag.put(key.toString(), homeTag);
        });
    }

    @Override
    public void loadData(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> {
            CompoundTag homeTag = tag.getCompound(key);
            Map<String, GlobalPos> homeMap = new HashMap<>();
            homeTag.getAllKeys().forEach(homeKey -> homeMap.put(homeKey, ModUtils.fromTag(homeTag.getCompound(homeKey))));
            homes.put(UUID.fromString(key), homeMap);
        });
    }
}
