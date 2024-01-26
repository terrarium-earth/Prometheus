package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.bytecodecs.defaults.MapCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.Consumer;

public record ServerboundMemberRolesPacket(UUID target,
                                           Object2BooleanMap<UUID> ids) implements Packet<ServerboundMemberRolesPacket> {

    public static final ServerboundPacketType<ServerboundMemberRolesPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundMemberRolesPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundMemberRolesPacket> implements ServerboundPacketType<ServerboundMemberRolesPacket> {

        public Type() {
            super(
                ServerboundMemberRolesPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "member_roles"),
                ObjectByteCodec.create(
                    ByteCodec.UUID.fieldOf(ServerboundMemberRolesPacket::target),
                    new MapCodec<>(ByteCodec.UUID, ByteCodec.BOOLEAN).map(map -> {
                            Object2BooleanMap<UUID> ids = new Object2BooleanOpenHashMap<>(map.size());
                            ids.putAll(map);
                            return ids;
                        }, map -> map
                    ).fieldOf(ServerboundMemberRolesPacket::ids),
                    ServerboundMemberRolesPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundMemberRolesPacket message) {
            return player -> {
                for (UUID id : message.ids.keySet()) {
                    if (!RoleHandler.canModifyRole(player, id)) {
                        player.sendSystemMessage(ConstantComponents.CANT_GIVE_ROLE);
                        return;
                    }
                }
                RoleHandler.changeRoles(player.level(), message.target, message.ids);
            };
        }
    }
}
