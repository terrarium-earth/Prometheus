package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenLocationScreenPacket;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundOpenLocationPacket(LocationType locationType) implements Packet<ServerboundOpenLocationPacket> {

    public static final ServerboundPacketType<ServerboundOpenLocationPacket> TYPE = CodecPacketType.Server.create(
        Prometheus.id("open_location"),
        ByteCodec.ofEnum(LocationType.class).map(ServerboundOpenLocationPacket::new, ServerboundOpenLocationPacket::locationType),
        NetworkHandle.handle((message, player) -> {
            if (player instanceof ServerPlayer serverPlayer) {
                switch (message.locationType()) {
                    case WARP -> ClientboundOpenLocationScreenPacket.openWarps(serverPlayer);
                    case HOME -> ClientboundOpenLocationScreenPacket.openHomes(serverPlayer);
                }
            }
        })
    );

    @Override
    public PacketType<ServerboundOpenLocationPacket> type() {
        return TYPE;
    }
}
