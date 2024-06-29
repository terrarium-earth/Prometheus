package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.ui.roles.editing.RoleEditingScreen;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;

public record ClientboundOpenRoleScreenPacket(
        RoleEditContent content
) implements Packet<ClientboundOpenRoleScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenRoleScreenPacket> TYPE = CodecPacketType.Client.create(
        Prometheus.id("open_role_screen"),
        RoleEditContent.BYTE_CODEC.map(ClientboundOpenRoleScreenPacket::new, ClientboundOpenRoleScreenPacket::content),
        NetworkHandle.handle(message -> RoleEditingScreen.open(message.content()))
    );

    @Override
    public PacketType<ClientboundOpenRoleScreenPacket> type() {
        return TYPE;
    }
}
