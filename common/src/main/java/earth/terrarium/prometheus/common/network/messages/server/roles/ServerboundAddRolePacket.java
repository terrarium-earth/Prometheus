package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record ServerboundAddRolePacket() implements Packet<ServerboundAddRolePacket> {

    public static final ServerboundPacketType<ServerboundAddRolePacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundAddRolePacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundAddRolePacket> {

        @Override
        public Class<ServerboundAddRolePacket> type() {
            return ServerboundAddRolePacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "add_role");
        }

        @Override
        public void encode(ServerboundAddRolePacket message, FriendlyByteBuf buffer) {}

        @Override
        public ServerboundAddRolePacket decode(FriendlyByteBuf buffer) {
            return new ServerboundAddRolePacket();
        }

        @Override
        public Consumer<Player> handle(ServerboundAddRolePacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && RoleHandler.canModifyRoles(player)) {
                    RoleHandler.setRole(player, null, new Role());
                    ServerboundOpenRolesPacket.openScreen(serverPlayer);
                } else {
                    player.sendSystemMessage(ConstantComponents.NOT_ALLOWED_TO_EDIT_ROLES);
                }
            };
        }
    }
}
