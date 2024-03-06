package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.ui.roles.main.RolesScreen;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundOpenRolesScreenPacket(
    RolesContent content) implements Packet<ClientboundOpenRolesScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenRolesScreenPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundOpenRolesScreenPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundOpenRolesScreenPacket> {

        @Override
        public Class<ClientboundOpenRolesScreenPacket> type() {
            return ClientboundOpenRolesScreenPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "open_roles_screen");
        }

        @Override
        public void encode(ClientboundOpenRolesScreenPacket message, FriendlyByteBuf buffer) {
            message.content().write(buffer);
        }

        @Override
        public ClientboundOpenRolesScreenPacket decode(FriendlyByteBuf buffer) {
            return new ClientboundOpenRolesScreenPacket(RolesContent.read(buffer));
        }

        @Override
        public Runnable handle(ClientboundOpenRolesScreenPacket message) {
            return () -> RolesScreen.open(message.content());
        }
    }
}
