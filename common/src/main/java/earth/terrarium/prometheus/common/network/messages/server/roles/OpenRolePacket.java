package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.OpenRoleScreenPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record OpenRolePacket(UUID id) implements Packet<OpenRolePacket> {

    public static final PacketHandler<OpenRolePacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_role");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenRolePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenRolePacket> {

        @Override
        public void encode(OpenRolePacket message, FriendlyByteBuf buffer) {
            buffer.writeUUID(message.id());
        }

        @Override
        public OpenRolePacket decode(FriendlyByteBuf buffer) {
            return new OpenRolePacket(buffer.readUUID());
        }

        @Override
        public PacketContext handle(OpenRolePacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2)) {
                    Set<UUID> editable = RoleHandler.getEditableRoles(player);
                    if (!editable.contains(message.id())) {
                        player.sendSystemMessage(ConstantComponents.CANT_EDIT_ROLE);
                        return;
                    }
                    List<RoleEntry> roles = RoleHandler.roles(player.level()).roles().stream()
                        .filter(entry -> editable.contains(entry.id()))
                        .toList();
                    NetworkHandler.CHANNEL.sendToPlayer(new OpenRoleScreenPacket(new RoleEditContent(roles, message.id())), player);
                }
            };
        }
    }
}
