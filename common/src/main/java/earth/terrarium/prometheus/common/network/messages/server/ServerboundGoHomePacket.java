package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import earth.terrarium.prometheus.common.handlers.locations.HomeHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenLocationScreenPacket;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundGoHomePacket() implements Packet<ServerboundGoHomePacket> {

    public static final ServerboundPacketType<ServerboundGoHomePacket> TYPE = CodecPacketType.Server.create(
            Prometheus.id("go_home"),
            ByteCodec.unit(ServerboundGoHomePacket::new),
            NetworkHandle.handle((message, player) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (!HomeHandler.teleport(serverPlayer)) {
                        var homes = LocationsApi.API.getHomes(serverPlayer);
                        if (homes.size() > 1) {
                            ClientboundOpenLocationScreenPacket.openHomes(serverPlayer);
                        }
                    }
                }
            })
    );

    @Override
    public PacketType<ServerboundGoHomePacket> type() {
        return TYPE;
    }
}
