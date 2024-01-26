package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public record ServerboundChangeRolesPacket(List<UUID> ids) implements Packet<ServerboundChangeRolesPacket> {

    public static final ServerboundPacketType<ServerboundChangeRolesPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundChangeRolesPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundChangeRolesPacket> implements ServerboundPacketType<ServerboundChangeRolesPacket> {

        public Type() {
            super(
                ServerboundChangeRolesPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "change_roles"),
                ObjectByteCodec.create(
                    ByteCodec.UUID.listOf().fieldOf(ServerboundChangeRolesPacket::ids),
                    ServerboundChangeRolesPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundChangeRolesPacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && RoleHandler.canModifyRoles(player)) {
                    Set<UUID> editableRoles = RoleHandler.getEditableRoles(player);
                    Set<UUID> roles = RoleHandler.roles(player.level()).ids();
                    //Check if message.ids contains any roles that the player cannot edit
                    if (!isValid(editableRoles, roles, message.ids)) {
                        player.sendSystemMessage(ConstantComponents.CANT_EDIT_ROLE_IN_LIST);
                        serverPlayer.closeContainer();
                    } else {
                        RoleHandler.reorder(player, message.ids);
                        ServerboundOpenRolesPacket.openScreen(serverPlayer);
                    }
                }
            };
        }

        private static boolean isValid(Set<UUID> editable, Set<UUID> roles, List<UUID> ids) {
            List<UUID> unmodifiable = roles.stream().filter(uuid -> !editable.contains(uuid)).toList();
            if (unmodifiable.size() > ids.size()) {
                return false;
            }
            for (int i = 0; i < unmodifiable.size(); i++) {
                if (!unmodifiable.get(i).equals(ids.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
