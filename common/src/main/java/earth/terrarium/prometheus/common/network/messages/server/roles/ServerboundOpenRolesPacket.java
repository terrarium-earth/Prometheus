package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenRolesScreenPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public record ServerboundOpenRolesPacket() implements Packet<ServerboundOpenRolesPacket> {

    public static final ServerboundPacketType<ServerboundOpenRolesPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundOpenRolesPacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundOpenRolesPacket> {

        @Override
        public Class<ServerboundOpenRolesPacket> type() {
            return ServerboundOpenRolesPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "open_roles");
        }

        @Override
        public void encode(ServerboundOpenRolesPacket message, FriendlyByteBuf buffer) {}

        @Override
        public ServerboundOpenRolesPacket decode(FriendlyByteBuf buffer) {
            return new ServerboundOpenRolesPacket();
        }

        @Override
        public Consumer<Player> handle(ServerboundOpenRolesPacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    openScreen(serverPlayer);
                }
            };
        }
    }

    public static void openScreen(ServerPlayer player) {
        if (!player.hasPermissions(2)) return;
        Set<UUID> editable = RoleHandler.getEditableRoles(player);
        List<RoleEntry> roles = RoleHandler.roles(player.level()).roles();
        for (RoleEntry role : roles) {
            if (editable.contains(role.id())) {
                NetworkHandler.CHANNEL.sendToPlayer(new ClientboundOpenRolesScreenPacket(new RolesContent(roles, roles.indexOf(role))), player);
                return;
            }
        }
    }
}
