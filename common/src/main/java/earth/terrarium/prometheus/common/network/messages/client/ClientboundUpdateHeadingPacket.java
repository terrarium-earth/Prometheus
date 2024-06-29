package earth.terrarium.prometheus.common.network.messages.client;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.utils.ClientListenerHook;
import earth.terrarium.prometheus.common.handlers.heading.HeadingData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import java.util.List;

public record ClientboundUpdateHeadingPacket(
        List<HeadingData> headings
) implements Packet<ClientboundUpdateHeadingPacket> {

    public static final ClientboundPacketType<ClientboundUpdateHeadingPacket> TYPE = CodecPacketType.Client.create(
            Prometheus.id("update_heading"),
            HeadingData.BYTE_CODEC.listOf().map(ClientboundUpdateHeadingPacket::new, ClientboundUpdateHeadingPacket::headings),
            NetworkHandle.handle(message -> {
                ClientPacketListener listener = Minecraft.getInstance().getConnection();
                if (listener instanceof ClientListenerHook hook) {
                    message.headings.forEach(data -> {
                        hook.prometheus$setHeading(data.id(), data.heading());
                        hook.prometheus$setHeadingText(data.id(), data.text());
                    });
                }
            })
    );

    @Override
    public PacketType<ClientboundUpdateHeadingPacket> type() {
        return TYPE;
    }
}
