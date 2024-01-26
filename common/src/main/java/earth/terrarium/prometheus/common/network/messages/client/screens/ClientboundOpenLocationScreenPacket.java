package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.location.LocationScreen;
import earth.terrarium.prometheus.common.menus.content.location.LocationContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundOpenLocationScreenPacket(
    LocationContent content) implements Packet<ClientboundOpenLocationScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenLocationScreenPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundOpenLocationScreenPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundOpenLocationScreenPacket> {

        @Override
        public Class<ClientboundOpenLocationScreenPacket> type() {
            return ClientboundOpenLocationScreenPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "open_location_screen");
        }

        @Override
        public void encode(ClientboundOpenLocationScreenPacket message, FriendlyByteBuf buffer) {
            message.content().write(buffer);
        }

        @Override
        public ClientboundOpenLocationScreenPacket decode(FriendlyByteBuf buffer) {
            return new ClientboundOpenLocationScreenPacket(LocationContent.read(buffer));
        }

        @Override
        public Runnable handle(ClientboundOpenLocationScreenPacket message) {
            return () -> LocationScreen.open(message.content());
        }
    }
}
