package earth.terrarium.prometheus.common.network.messages.server.roles;

import com.mojang.logging.LogUtils;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.UUID;

public record SaveRolePacket(UUID id, Role role) implements Packet<SaveRolePacket> {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final PacketHandler<SaveRolePacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "save_role");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SaveRolePacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<SaveRolePacket> {

        @Override
        public void encode(SaveRolePacket message, FriendlyByteBuf buffer) {
            PacketHelper.writeWithYabn(buffer, Role.CODEC, message.role(), true);
            buffer.writeUUID(message.id());
        }

        @Override
        public SaveRolePacket decode(FriendlyByteBuf buffer) {
            Role role = PacketHelper.readWithYabn(buffer, Role.CODEC, true).get()
                .ifRight(error -> LOGGER.error("Error reading role: {}", error))
                .left()
                .orElse(null);
            return new SaveRolePacket(buffer.readUUID(), role);
        }

        @Override
        public PacketContext handle(SaveRolePacket message) {
            return (player, level) -> {
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
