package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Set;
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
                    Set<UUID> editableRoles = RoleHandler.getEditableRoles(player);
                    Set<UUID> roles = RoleHandler.roles(player).ids();
                    //Check if message.ids contains any roles that the player cannot edit
                    if (!isValid(editableRoles, roles, message.ids)) {
                        player.sendSystemMessage(ConstantComponents.CANT_EDIT_ROLE_IN_LIST);
                        serverPlayer.closeContainer();
                    } else {
                        RoleHandler.reorder(player, message.ids);
                        OpenRolesPacket.openScreen(serverPlayer);
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
