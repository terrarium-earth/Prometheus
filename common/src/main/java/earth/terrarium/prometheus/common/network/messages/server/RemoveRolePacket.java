package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.commands.admin.RolesCommand;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public record RemoveRolePacket(List<UUID> ids) implements Packet<RemoveRolePacket> {

    public static final PacketHandler<RemoveRolePacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "remove_role");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<RemoveRolePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<RemoveRolePacket> {

        @Override
        public void encode(RemoveRolePacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(message.ids, FriendlyByteBuf::writeUUID);
        }

        @Override
        public RemoveRolePacket decode(FriendlyByteBuf buffer) {
            return new RemoveRolePacket(buffer.readList(FriendlyByteBuf::readUUID));
        }

        @Override
        public PacketContext handle(RemoveRolePacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer && RoleHandler.canModifyRoles(player)) {
                    boolean loserTriedToRemoveTheirHighestRole = false;
                    for (UUID id : message.ids) {
                        Role role = RoleHandler.getRole(player, id);
                        if (RoleHandler.getHighestRole(player) == role) {
                            loserTriedToRemoveTheirHighestRole = true;
                        } else {
                            RoleHandler.removeRole(player, id);
                        }
                    }
                    if (loserTriedToRemoveTheirHighestRole) {
                        player.sendSystemMessage(Component.literal("You cannot remove your highest role!"));
                        serverPlayer.closeContainer();
                    } else {
                        RolesCommand.openRolesMenu(serverPlayer);
                    }
                }
            };
        }
    }
}
