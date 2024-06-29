package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.MemberRolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenMemberRolesScreenPacket;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public record ServerboundOpenMemberRolesPacket(UUID id) implements Packet<ServerboundOpenMemberRolesPacket> {

    public static final ServerboundPacketType<ServerboundOpenMemberRolesPacket> TYPE = CodecPacketType.Server.create(
        Prometheus.id("open_member_roles"),
        ByteCodec.UUID.map(ServerboundOpenMemberRolesPacket::new, ServerboundOpenMemberRolesPacket::id),
        NetworkHandle.handle((message, player) -> {
            if (player.hasPermissions(2)) {
                List<RoleEntry> roles = RoleHandler.roles(player.level()).roles();
                Set<UUID> editable = RoleHandler.getEditableRoles(player);
                Set<UUID> selected = RoleHandler.getRolesForPlayer(player, message.id());

                List<MemberRolesContent.MemberRole> packetRoles = roles.stream()
                    .filter(Predicate.not(RoleEntry::isDefault))
                    .map(entry -> MemberRolesContent.MemberRole.of(entry, selected.contains(entry.id()), editable.contains(entry.id())))
                    .toList();

                NetworkHandler.CHANNEL.sendToPlayer(new ClientboundOpenMemberRolesScreenPacket(new MemberRolesContent(
                    packetRoles,
                    message.id()
                )), player);
            }
        })
    );

    @Override
    public PacketType<ServerboundOpenMemberRolesPacket> type() {
        return TYPE;
    }
}
