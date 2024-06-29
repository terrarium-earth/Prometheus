package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
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

import java.util.UUID;

public record ServerboundSaveRolePacket(
        UUID id,
        Role role
) implements Packet<ServerboundSaveRolePacket> {

    public static final ServerboundPacketType<ServerboundSaveRolePacket> TYPE = CodecPacketType.Server.create(
        Prometheus.id("save_role"),
        ObjectByteCodec.create(
            ByteCodec.UUID.fieldOf(ServerboundSaveRolePacket::id),
            Role.BYTE_CODEC.fieldOf(ServerboundSaveRolePacket::role),
            ServerboundSaveRolePacket::new
        ),
        NetworkHandle.handle((message, player) -> {
            if (player instanceof ServerPlayer serverPlayer && RoleHandler.canModifyRoles(player)) {
                if (RoleHandler.getEditableRoles(player).contains(message.id())) {
                    RoleHandler.setRole(serverPlayer, message.id(), message.role());
                } else {
                    player.sendSystemMessage(ConstantComponents.CANT_EDIT_ROLE);
                }
            } else {
                player.sendSystemMessage(ConstantComponents.NOT_ALLOWED_TO_EDIT_ROLES);
            }
        })
    );

    @Override
    public PacketType<ServerboundSaveRolePacket> type() {
        return TYPE;
    }
}
