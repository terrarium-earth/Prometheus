package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenRolesScreenPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ServerboundOpenRolesPacket() implements Packet<ServerboundOpenRolesPacket> {

    public static final ServerboundPacketType<ServerboundOpenRolesPacket> TYPE = CodecPacketType.Server.create(
            Prometheus.id("open_roles"),
            ByteCodec.unit(ServerboundOpenRolesPacket::new),
            NetworkHandle.handle((message, player) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    openScreen(serverPlayer);
                }
            })
    );

    @Override
    public PacketType<ServerboundOpenRolesPacket> type() {
        return TYPE;
    }

    public static void openScreen(ServerPlayer player) {
        if (!RoleHandler.canModifyRoles(player)) return;
        Set<UUID> editable = RoleHandler.getEditableRoles(player);
        List<RoleEntry> roles = RoleHandler.roles(player.level()).roles();
        for (RoleEntry role : roles) {
            if (editable.contains(role.id())) {
                NetworkHandler.CHANNEL.sendToPlayer(new ClientboundOpenRolesScreenPacket(new RolesContent(roles, roles.indexOf(role))), player);
                return;
            }
        }
    }
}
