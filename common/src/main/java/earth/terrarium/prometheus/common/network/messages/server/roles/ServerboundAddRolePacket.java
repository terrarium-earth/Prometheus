package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundAddRolePacket() implements Packet<ServerboundAddRolePacket> {

    public static final ServerboundPacketType<ServerboundAddRolePacket> TYPE = CodecPacketType.Server.create(
            Prometheus.id("add_role"),
            ByteCodec.unit(ServerboundAddRolePacket::new),
            NetworkHandle.handle((message, player) -> {
                if (player instanceof ServerPlayer serverPlayer && RoleHandler.canModifyRoles(player)) {
                    RoleHandler.setRole(player, null, new Role());
                    ServerboundOpenRolesPacket.openScreen(serverPlayer);
                } else {
                    player.sendSystemMessage(ConstantComponents.NOT_ALLOWED_TO_EDIT_ROLES);
                }
            })
    );

    @Override
    public PacketType<ServerboundAddRolePacket> type() {
        return TYPE;
    }
}
