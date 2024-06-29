package earth.terrarium.prometheus.common.handlers.locations;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.utils.files.CodecSavedData;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class WarpHandler {

    private static final CodecSavedData.Factory<Map<String, GlobalPos>> DATA = CodecSavedData
        .create(Codec.unboundedMap(Codec.STRING, GlobalPos.CODEC), "prometheus_warps")
        .defaultValue(HashMap::new)
        .global()
        .clean();

    public static boolean add(ServerPlayer player, String name) {
        if (canModifyWarps(player)) {
            var data = DATA.create(player.serverLevel());
            Map<String, GlobalPos> warps = data.get();
            if (warps.containsKey(name)) {
                player.sendSystemMessage(ConstantComponents.WARP_ALREADY_EXISTS);
                return false;
            }
            warps.put(name, GlobalPos.of(player.level().dimension(), player.blockPosition()));
            data.setDirty();
            return true;
        }
        return false;
    }

    public static void remove(ServerPlayer player, String name) {
        var data = DATA.create(player.serverLevel());
        data.get().remove(name);
        data.setDirty();
    }

    public static Map<String, GlobalPos> getWarps(MinecraftServer server) {
        return DATA.create(server.overworld()).get();
    }

    public static boolean canModifyWarps(ServerPlayer player) {
        return PermissionApi.API.getPermission(player, "warps.manage").map(player.hasPermissions(2));
    }
}
