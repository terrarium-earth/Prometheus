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

public record ServerboundAddLocationPacket(LocationType locationType,
                                           String name) implements Packet<ServerboundAddLocationPacket> {

    public static final ServerboundPacketType<ServerboundAddLocationPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundAddLocationPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundAddLocationPacket> implements ServerboundPacketType<ServerboundAddLocationPacket> {

        public Type() {
            super(
                ServerboundAddLocationPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "add_location"),
                ObjectByteCodec.create(
                    ByteCodec.ofEnum(LocationType.class).fieldOf(ServerboundAddLocationPacket::locationType),
                    ByteCodec.STRING.fieldOf(ServerboundAddLocationPacket::name),
                    ServerboundAddLocationPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundAddLocationPacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    switch (message.locationType) {
                        case HOME -> {
                            if (HomeHandler.add(serverPlayer, message.name)) {
                                ServerboundOpenLocationPacket.openHomes(serverPlayer);
                            }
                        }
                        case WARP -> {
                            if (WarpHandler.add(serverPlayer, message.name)) {
                                ServerboundOpenLocationPacket.openWarps(serverPlayer);
                            }
                        }
                    }
                }
            };
        }
    }
}
