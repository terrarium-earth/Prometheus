package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.commands.utilities.RtpCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;

public record ServerboundGoSpawnPacket() implements Packet<ServerboundGoSpawnPacket> {

    public static final ServerboundPacketType<ServerboundGoSpawnPacket> TYPE = CodecPacketType.Server.create(
            Prometheus.id("go_spawn"),
            ByteCodec.unit(ServerboundGoSpawnPacket::new),
            NetworkHandle.handle((message, player) -> {
                if (player instanceof ServerPlayer serverPlayer && !PermissionApi.API.getPermission(player, "commands.spawn").isFalse()) {
                    final BlockPos originalPos = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, player.level().getSharedSpawnPos());
                    BlockPos pos = RtpCommand.tp(originalPos, serverPlayer, 10, 0);
                    if (pos == null) {
                        pos = originalPos;
                    }
                    serverPlayer.teleportTo(serverPlayer.server.overworld(), pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, player.getYRot(), player.getXRot());
                }
            })
    );

    @Override
    public PacketType<ServerboundGoSpawnPacket> type() {
        return TYPE;
    }
}
