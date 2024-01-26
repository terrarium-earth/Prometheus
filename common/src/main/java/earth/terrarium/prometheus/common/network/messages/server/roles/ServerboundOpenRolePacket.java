package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenRoleScreenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public record ServerboundOpenRolePacket(UUID id) implements Packet<ServerboundOpenRolePacket> {

    public static final ServerboundPacketType<ServerboundOpenRolePacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundOpenRolePacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundOpenRolePacket> implements ServerboundPacketType<ServerboundOpenRolePacket> {

        public Type() {
            super(
                ServerboundOpenRolePacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "open_role"),
                ObjectByteCodec.create(
                    ByteCodec.UUID.fieldOf(ServerboundOpenRolePacket::id),
                    ServerboundOpenRolePacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundOpenRolePacket message) {
            return player -> {
                if (player.hasPermissions(2)) {
                    Set<UUID> editable = RoleHandler.getEditableRoles(player);
                    if (!editable.contains(message.id())) {
                        player.sendSystemMessage(ConstantComponents.CANT_EDIT_ROLE);
                        return;
                    }
                    List<RoleEntry> roles = RoleHandler.roles(player.level()).roles().stream()
                        .filter(entry -> editable.contains(entry.id()))
                        .collect(Collectors.toList());
                    NetworkHandler.CHANNEL.sendToPlayer(new ClientboundOpenRoleScreenPacket(new RoleEditContent(roles, message.id())), player);
                }
            };
        }
    }
}
