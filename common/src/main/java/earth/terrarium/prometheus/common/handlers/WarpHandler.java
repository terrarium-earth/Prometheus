package earth.terrarium.prometheus.common.handlers;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
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

public class WarpHandler extends SaveHandler {

    private static final WarpHandler CLIENT_SIDE = new WarpHandler();

    private final Map<String, GlobalPos> warps = new HashMap<>();

    public static WarpHandler read(Level level) {
        return read(level, CLIENT_SIDE, WarpHandler::new, "prometheus_warps");
    }

    public static boolean add(ServerPlayer player, String name) {
        if (canModifyWarps(player)) {
            Map<String, GlobalPos> warps = getWarps(player);
            if (warps.containsKey(name)) {
                player.sendSystemMessage(ConstantComponents.WARP_ALREADY_EXISTS);
                return false;
            }
            warps.put(name, GlobalPos.of(player.level().dimension(), player.blockPosition()));
            read(player.level()).setDirty();
            return true;
        }
        return false;
    }

    public static void remove(ServerPlayer player, String name) {
        getWarps(player).remove(name);
        read(player.level()).setDirty();
    }

    public static void teleport(ServerPlayer player, String name) {
        WarpHandler data = read(player.level());
        GlobalPos pos = data.warps.get(name);
        if (pos == null) {
            player.sendSystemMessage(ConstantComponents.WARP_DOES_NOT_EXIST);
            return;
        }
        ServerLevel level = player.server.getLevel(pos.dimension());
        if (level == null) {
            player.sendSystemMessage(ConstantComponents.NO_DIMENSION);
            return;
        }

        player.teleportTo(level, pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), player.getYRot(), player.getXRot());
    }

    public static Map<String, GlobalPos> getWarps(Player player) {
        return read(player.level()).warps;
    }

    public static boolean canModifyWarps(ServerPlayer player) {
        return PermissionApi.API.getPermission(player, "warps.manage").map(player.hasPermissions(2));
    }

    @Override
    public void saveData(@NotNull CompoundTag tag) {
        warps.forEach((key, value) -> tag.put(key, ModUtils.toTag(value)));
    }

    @Override
    public void loadData(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> warps.put(key, ModUtils.fromTag(tag.getCompound(key))));
    }
}
