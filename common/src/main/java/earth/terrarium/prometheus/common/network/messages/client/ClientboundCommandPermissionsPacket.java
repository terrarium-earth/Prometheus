package earth.terrarium.prometheus.common.network.messages.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissions;

import java.util.List;

public record ClientboundCommandPermissionsPacket(
        List<String> permissions
) implements Packet<ClientboundCommandPermissionsPacket> {

    public static final ClientboundPacketType<ClientboundCommandPermissionsPacket> TYPE = CodecPacketType.Client.create(
            Prometheus.id("command_permissions"),
            ObjectByteCodec.create(
                    ByteCodec.STRING.listOf().fieldOf(ClientboundCommandPermissionsPacket::permissions),
                    ClientboundCommandPermissionsPacket::new
            ),
            NetworkHandle.handle(message -> CommandPermissions.setCommandPermissions(message.permissions))
    );

    @Override
    public PacketType<ClientboundCommandPermissionsPacket> type() {
        return TYPE;
    }
}
