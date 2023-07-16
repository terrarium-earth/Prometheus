package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.HomeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record GoHomePacket() implements Packet<GoHomePacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "go_home");
    public static final PacketHandler<GoHomePacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<GoHomePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<GoHomePacket> {

        @Override
        public void encode(GoHomePacket message, FriendlyByteBuf buffer) {}

        @Override
        public GoHomePacket decode(FriendlyByteBuf buffer) {
            return new GoHomePacket();
        }

        @Override
        public PacketContext handle(GoHomePacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (!HomeHandler.teleport(serverPlayer)) {
                        var homes = HomeHandler.getHomes(player);
                        if (homes.size() > 1) {
                            OpenLocationPacket.openHomes(serverPlayer);
                        }
                    }
                }
            };
        }
    }
}
