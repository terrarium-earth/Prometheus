package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.commands.utilities.RtpCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Consumer;

public record ServerboundGoSpawnPacket() implements Packet<ServerboundGoSpawnPacket> {

    public static final ServerboundPacketType<ServerboundGoSpawnPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundGoSpawnPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundGoSpawnPacket> {

        @Override
        public Class<ServerboundGoSpawnPacket> type() {
            return ServerboundGoSpawnPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "go_spawn");
        }

        @Override
        public void encode(ServerboundGoSpawnPacket message, FriendlyByteBuf buffer) {}

        @Override
        public ServerboundGoSpawnPacket decode(FriendlyByteBuf buffer) {
            return new ServerboundGoSpawnPacket();
        }

        @Override
        public Consumer<Player> handle(ServerboundGoSpawnPacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && !PermissionApi.API.getPermission(player, "commands.spawn").isFalse()) {
                    final BlockPos originalPos = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, player.level().getSharedSpawnPos());
                    BlockPos pos = RtpCommand.tp(originalPos, serverPlayer, 10, 0);
                    if (pos == null) {
                        pos = originalPos;
                    }
                    serverPlayer.teleportTo(serverPlayer.server.overworld(), pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, player.getYRot(), player.getXRot());
                }
            };
        }
    }
}
