package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.locations.HomeHandler;
import earth.terrarium.prometheus.common.handlers.locations.WarpHandler;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record DeleteLocationPacket(LocationType type, String name) implements Packet<DeleteLocationPacket> {

    public static final PacketHandler<DeleteLocationPacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "delete_location");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<DeleteLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<DeleteLocationPacket> {

        @Override
        public void encode(DeleteLocationPacket message, FriendlyByteBuf buffer) {
            buffer.writeEnum(message.type());
            buffer.writeUtf(message.name());
        }

        @Override
        public DeleteLocationPacket decode(FriendlyByteBuf buffer) {
            return new DeleteLocationPacket(buffer.readEnum(LocationType.class), buffer.readUtf());
        }

        @Override
        public PacketContext handle(DeleteLocationPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    switch (message.type) {
                        case HOME -> {
                            HomeHandler.remove(serverPlayer, message.name);
                            OpenLocationPacket.openHomes(serverPlayer);
                        }
                        case WARP -> {
                            if (WarpHandler.canModifyWarps(serverPlayer)) {
                                WarpHandler.remove(serverPlayer, message.name);
                                OpenLocationPacket.openWarps(serverPlayer);
                            }
                        }
                    }
                }
            };
        }
    }
}
