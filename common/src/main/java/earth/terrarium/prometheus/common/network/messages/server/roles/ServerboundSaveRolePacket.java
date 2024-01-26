package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.function.Consumer;

public record ServerboundSaveRolePacket(UUID id, Role role) implements Packet<ServerboundSaveRolePacket> {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ServerboundPacketType<ServerboundSaveRolePacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundSaveRolePacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundSaveRolePacket> {

        @Override
        public Class<ServerboundSaveRolePacket> type() {
            return ServerboundSaveRolePacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "save_role");
        }

        @Override
        public void encode(ServerboundSaveRolePacket message, FriendlyByteBuf buf) {
            PacketHelper.writeWithYabn(buf, Role.CODEC, message.role(), true);
            buf.writeUUID(message.id());
        }

        @Override
        public ServerboundSaveRolePacket decode(FriendlyByteBuf buf) {
            Role role = PacketHelper.readWithYabn(buf, Role.CODEC, true).get()
                .ifRight(error -> LOGGER.error("Error reading role: {}", error))
                .left()
                .orElse(null);
            return new ServerboundSaveRolePacket(buf.readUUID(), role);
        }

        @Override
        public Consumer<Player> handle(ServerboundSaveRolePacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && RoleHandler.canModifyRoles(player)) {
                    if (RoleHandler.getEditableRoles(player).contains(message.id())) {
                        RoleHandler.setRole(serverPlayer, message.id(), message.role());
                    } else {
                        player.sendSystemMessage(ConstantComponents.CANT_EDIT_ROLE);
                    }
                } else {
                    player.sendSystemMessage(ConstantComponents.NOT_ALLOWED_TO_EDIT_ROLES);
                }
            };
        }
    }
}
