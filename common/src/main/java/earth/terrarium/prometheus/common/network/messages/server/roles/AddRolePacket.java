package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record AddRolePacket() implements Packet<AddRolePacket> {

    public static final PacketHandler<AddRolePacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "add_role");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<AddRolePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<AddRolePacket> {

        @Override
        public void encode(AddRolePacket message, FriendlyByteBuf buffer) {}

        @Override
        public AddRolePacket decode(FriendlyByteBuf buffer) {
            return new AddRolePacket();
        }

        @Override
        public PacketContext handle(AddRolePacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer && RoleHandler.canModifyRoles(player)) {
                    RoleHandler.setRole(player, null, new Role());
                    OpenRolesPacket.openScreen(serverPlayer);
                } else {
                    player.sendSystemMessage(ConstantComponents.NOT_ALLOWED_TO_EDIT_ROLES);
                }
            };
        }
    }
}
