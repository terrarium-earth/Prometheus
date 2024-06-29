package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenRoleScreenPacket;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ServerboundOpenRolePacket(UUID id) implements Packet<ServerboundOpenRolePacket> {

    public static final ServerboundPacketType<ServerboundOpenRolePacket> TYPE = CodecPacketType.Server.create(
        Prometheus.id("open_role"),
        ByteCodec.UUID.map(ServerboundOpenRolePacket::new, ServerboundOpenRolePacket::id),
        NetworkHandle.handle((message, player) -> {
            if (player.hasPermissions(2) || RoleHandler.canModifyRoles(player)) {
                Set<UUID> editable = RoleHandler.getEditableRoles(player);
                if (!editable.contains(message.id())) {
                    player.sendSystemMessage(ConstantComponents.CANT_EDIT_ROLE);
                    return;
                }
                List<RoleEntry> roles = RoleHandler.roles(player.level()).roles().stream()
                    .filter(entry -> editable.contains(entry.id()))
                    .collect(Collectors.toList());
                NetworkHandler.CHANNEL.sendToPlayer(new ClientboundOpenRoleScreenPacket(new RoleEditContent(roles, message.id())), player);
            }
        })
    );

    @Override
    public PacketType<ServerboundOpenRolePacket> type() {
        return TYPE;
    }
}
