package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.ui.roles.adding.MemberEditingScreen;
import earth.terrarium.prometheus.common.menus.content.MemberRolesContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundOpenMemberRolesScreenPacket(
    MemberRolesContent content) implements Packet<ClientboundOpenMemberRolesScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenMemberRolesScreenPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundOpenMemberRolesScreenPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundOpenMemberRolesScreenPacket> {

        @Override
        public Class<ClientboundOpenMemberRolesScreenPacket> type() {
            return ClientboundOpenMemberRolesScreenPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "open_member_roles_screen");
        }

        @Override
        public void encode(ClientboundOpenMemberRolesScreenPacket message, FriendlyByteBuf buffer) {
            message.content().write(buffer);
        }

        @Override
        public ClientboundOpenMemberRolesScreenPacket decode(FriendlyByteBuf buffer) {
            return new ClientboundOpenMemberRolesScreenPacket(MemberRolesContent.read(buffer));
        }

        @Override
        public Runnable handle(ClientboundOpenMemberRolesScreenPacket message) {
            return () -> MemberEditingScreen.open(message.content());
        }
    }
}
