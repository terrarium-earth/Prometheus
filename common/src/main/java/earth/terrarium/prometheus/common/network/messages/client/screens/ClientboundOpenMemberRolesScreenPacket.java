package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.ui.roles.adding.MemberEditingScreen;
import earth.terrarium.prometheus.common.menus.content.MemberRolesContent;

public record ClientboundOpenMemberRolesScreenPacket(
    MemberRolesContent content
) implements Packet<ClientboundOpenMemberRolesScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenMemberRolesScreenPacket> TYPE = CodecPacketType.Client.create(
            Prometheus.id("open_member_roles_screen"),
            MemberRolesContent.BYTE_CODEC.map(ClientboundOpenMemberRolesScreenPacket::new, ClientboundOpenMemberRolesScreenPacket::content),
            NetworkHandle.handle(message -> MemberEditingScreen.open(message.content()))
    );

    @Override
    public PacketType<ClientboundOpenMemberRolesScreenPacket> type() {
        return TYPE;
    }
}
