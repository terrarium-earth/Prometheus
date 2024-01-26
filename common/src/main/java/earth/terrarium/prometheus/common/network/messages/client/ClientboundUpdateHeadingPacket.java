package earth.terrarium.prometheus.common.network.messages.client;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.utils.ClientListenerHook;
import earth.terrarium.prometheus.common.handlers.heading.HeadingData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ClientboundUpdateHeadingPacket(
    List<HeadingData> headings) implements Packet<ClientboundUpdateHeadingPacket> {

    public static final ClientboundPacketType<ClientboundUpdateHeadingPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundUpdateHeadingPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundUpdateHeadingPacket> {

        @Override
        public Class<ClientboundUpdateHeadingPacket> type() {
            return ClientboundUpdateHeadingPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "update_heading");
        }

        public void encode(ClientboundUpdateHeadingPacket message, FriendlyByteBuf buf) {
            buf.writeCollection(message.headings, (buffer, pair) -> pair.write(buffer));
        }

        @Override
        public ClientboundUpdateHeadingPacket decode(FriendlyByteBuf buf) {
            return new ClientboundUpdateHeadingPacket(buf.readList(HeadingData::read));
        }

        @Override
        public Runnable handle(ClientboundUpdateHeadingPacket message) {
            return () -> {
                ClientPacketListener listener = Minecraft.getInstance().getConnection();
                if (listener instanceof ClientListenerHook hook) {
                    message.headings.forEach(data -> {
                        hook.prometheus$setHeading(data.id(), data.heading());
                        hook.prometheus$setHeadingText(data.id(), data.text());
                    });
                }
            };
        }
    }
}
