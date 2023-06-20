package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.OpenRolesScreenPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record OpenRolesPacket() implements Packet<OpenRolesPacket> {

    public static final PacketHandler<OpenRolesPacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_roles");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenRolesPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenRolesPacket> {

        @Override
        public void encode(OpenRolesPacket message, FriendlyByteBuf buffer) {}

        @Override
        public OpenRolesPacket decode(FriendlyByteBuf buffer) {
            return new OpenRolesPacket();
        }

        @Override
        public PacketContext handle(OpenRolesPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    openScreen(serverPlayer);
                }
            };
        }
    }

    public static void openScreen(ServerPlayer player) {
        if (!player.hasPermissions(2)) return;
        Set<UUID> editable = RoleHandler.getEditableRoles(player);
        List<RoleEntry> roles = RoleHandler.roles(player).roles();
        for (RoleEntry role : roles) {
            if (editable.contains(role.id())) {
                NetworkHandler.CHANNEL.sendToPlayer(new OpenRolesScreenPacket(new RolesContent(roles, roles.indexOf(role))), player);
                return;
            }
        }
    }
}
