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

public record ChangeRolesPacket(List<UUID> ids) implements Packet<ChangeRolesPacket> {

    public static final PacketHandler<ChangeRolesPacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "change_roles");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<ChangeRolesPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<ChangeRolesPacket> {

        @Override
        public void encode(ChangeRolesPacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(message.ids, FriendlyByteBuf::writeUUID);
        }

        @Override
        public ChangeRolesPacket decode(FriendlyByteBuf buffer) {
            return new ChangeRolesPacket(buffer.readList(FriendlyByteBuf::readUUID));
        }

        @Override
        public PacketContext handle(ChangeRolesPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer && RoleHandler.canModifyRoles(player)) {
                    Role highestRole = RoleHandler.getHighestRole(player);
                    UUID highestRoleUUID = RoleHandler.getRoles(player).getRoleId(highestRole);
                    if (highestRoleUUID != null && !message.ids.contains(highestRoleUUID)) {
                        player.sendSystemMessage(Component.literal("You cannot remove your highest role!"));
                        serverPlayer.closeContainer();
                    } else {
                        RoleHandler.reorder(player, message.ids);
                        RolesCommand.openRolesMenu(serverPlayer);
                    }
                }
            };
        }
    }
}
