package earth.terrarium.prometheus.common.handlers.locations;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.utils.files.CodecSavedData;
import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeHandler {

    private static final CodecSavedData.Factory<Map<UUID, Map<String, GlobalPos>>> DATA = CodecSavedData
        .create(Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.unboundedMap(Codec.STRING, GlobalPos.CODEC)), "prometheus_homes")
        .defaultValue(HashMap::new)
        .global()
        .clean();

    public static boolean add(ServerPlayer player, String name) {
        Map<String, GlobalPos> homes = getHomes(player);
        if (homes.size() >= RoleApi.API.getNonNullOption(player, HomeOptions.SERIALIZER).max()) {
            player.sendSystemMessage(ConstantComponents.MAX_HOMES);
            return false;
        } else if (homes.containsKey(name)) {
            player.sendSystemMessage(ConstantComponents.HOME_ALREADY_EXISTS);
            return false;
        }
        homes.put(name, GlobalPos.of(player.level().dimension(), player.blockPosition()));
        DATA.create(player.serverLevel()).setDirty();
        return true;
    }

    public static void remove(ServerPlayer player, String name) {
        Map<String, GlobalPos> homes = getHomes(player);
        if (!homes.containsKey(name)) {
            player.sendSystemMessage(ConstantComponents.HOME_DOES_NOT_EXIST);
            return;
        }
        homes.remove(name);
        DATA.create(player.serverLevel()).setDirty();
    }

    private static void teleport(ServerPlayer player, String name) {
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

        ModUtils.teleport(player, level, pos.pos().getX() + 0.5, pos.pos().getY(), pos.pos().getZ() + 0.5, player.getYRot(), player.getXRot());
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

    public static Map<String, GlobalPos> getHomes(ServerPlayer player) {
        var data = DATA.create(player.serverLevel());
        return data.get().computeIfAbsent(player.getUUID(), uuid -> new HashMap<>());
    }
}
