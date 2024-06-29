package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.ui.roles.main.RolesScreen;
import earth.terrarium.prometheus.common.menus.content.RolesContent;

public record ClientboundOpenRolesScreenPacket(
    RolesContent content) implements Packet<ClientboundOpenRolesScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenRolesScreenPacket> TYPE = CodecPacketType.Client.create(
        Prometheus.id("open_roles_screen"),
        RolesContent.BYTE_CODEC.map(ClientboundOpenRolesScreenPacket::new, ClientboundOpenRolesScreenPacket::content),
        NetworkHandle.handle(message -> RolesScreen.open(message.content()))
    );

    @Override
    public PacketType<ClientboundOpenRolesScreenPacket> type() {
        return TYPE;
    }
}
