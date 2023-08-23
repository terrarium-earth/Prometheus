package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.commands.utilities.RtpCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;

public record GoSpawnPacket() implements Packet<GoSpawnPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "go_spawn");
    public static final PacketHandler<GoSpawnPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<GoSpawnPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<GoSpawnPacket> {

        @Override
        public void encode(GoSpawnPacket message, FriendlyByteBuf buffer) {}

        @Override
        public GoSpawnPacket decode(FriendlyByteBuf buffer) {
            return new GoSpawnPacket();
        }

        @Override
        public PacketContext handle(GoSpawnPacket message) {
            return (player, level) -> {
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
