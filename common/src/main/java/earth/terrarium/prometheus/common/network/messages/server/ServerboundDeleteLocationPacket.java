package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.locations.HomeHandler;
import earth.terrarium.prometheus.common.handlers.locations.WarpHandler;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record ServerboundDeleteLocationPacket(LocationType locationType,
                                              String name) implements Packet<ServerboundDeleteLocationPacket> {

    public static final ServerboundPacketType<ServerboundDeleteLocationPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundDeleteLocationPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundDeleteLocationPacket> implements ServerboundPacketType<ServerboundDeleteLocationPacket> {

        public Type() {
            super(
                ServerboundDeleteLocationPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "delete_location"),
                ObjectByteCodec.create(
                    ByteCodec.ofEnum(LocationType.class).fieldOf(ServerboundDeleteLocationPacket::locationType),
                    ByteCodec.STRING.fieldOf(ServerboundDeleteLocationPacket::name),
                    ServerboundDeleteLocationPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundDeleteLocationPacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    switch (message.locationType) {
                        case HOME -> {
                            HomeHandler.remove(serverPlayer, message.name);
                            ServerboundOpenLocationPacket.openHomes(serverPlayer);
                        }
                        case WARP -> {
                            if (WarpHandler.canModifyWarps(serverPlayer)) {
                                WarpHandler.remove(serverPlayer, message.name);
                                ServerboundOpenLocationPacket.openWarps(serverPlayer);
                            }
                        }
                    }
                }
            };
        }
    }
}
