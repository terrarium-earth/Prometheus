package earth.terrarium.prometheus.api.locations;

import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record Location(ServerLevel level, BlockPos pos) {

    public void teleport(ServerPlayer player) {
        ModUtils.teleport(player, level, pos.getX(), pos.getY(), pos.getZ(), player.getYRot(), player.getXRot());
    }
}
