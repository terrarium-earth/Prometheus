package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.MemberRolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.OpenMemberRolesScreenPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public record OpenMemberRolesPacket(UUID id) implements Packet<OpenMemberRolesPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_member_roles");
    public static final PacketHandler<OpenMemberRolesPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenMemberRolesPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenMemberRolesPacket> {

        @Override
        public void encode(OpenMemberRolesPacket message, FriendlyByteBuf buffer) {
            buffer.writeUUID(message.id());
        }

        @Override
        public OpenMemberRolesPacket decode(FriendlyByteBuf buffer) {
            return new OpenMemberRolesPacket(buffer.readUUID());
        }

        @Override
        public PacketContext handle(OpenMemberRolesPacket message) {
            return (player, level) -> {
                if (player.hasPermissions(2)) {
                    List<RoleEntry> roles = RoleHandler.roles(player.level()).roles();
                    Set<UUID> editable = RoleHandler.getEditableRoles(player);
                    Set<UUID> selected = RoleHandler.getRolesForPlayer(player, message.id());

                    List<MemberRolesContent.MemberRole> packetRoles = roles.stream()
                        .filter(Predicate.not(RoleEntry::isDefault))
                        .map(entry -> MemberRolesContent.MemberRole.of(entry, selected.contains(entry.id()), editable.contains(entry.id())))
                        .toList();

                    NetworkHandler.CHANNEL.sendToPlayer(new OpenMemberRolesScreenPacket(new MemberRolesContent(
                        packetRoles,
                        message.id()
                    )), player);
                }
            };
        }
    }
}
