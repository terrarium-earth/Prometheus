package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record MemberRolesPacket(UUID target, Object2BooleanMap<UUID> ids) implements Packet<MemberRolesPacket> {

    public static final PacketHandler<MemberRolesPacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "member_roles");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<MemberRolesPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<MemberRolesPacket> {

        @Override
        public void encode(MemberRolesPacket message, FriendlyByteBuf buffer) {
            buffer.writeUUID(message.target);
            buffer.writeVarInt(message.ids.size());
            message.ids.forEach((id, bool) -> {
                buffer.writeUUID(id);
                buffer.writeBoolean(bool);
            });
        }

        @Override
        public MemberRolesPacket decode(FriendlyByteBuf buffer) {
            UUID id = buffer.readUUID();
            int size = buffer.readVarInt();
            Object2BooleanMap<UUID> map = new Object2BooleanOpenHashMap<>(size);
            for (int i = 0; i < size; i++) {
                map.put(buffer.readUUID(), buffer.readBoolean());
            }
            return new MemberRolesPacket(id, map);
        }

        @Override
        public PacketContext handle(MemberRolesPacket message) {
            return (player, level) -> {
                for (UUID id : message.ids.keySet()) {
                    if (!RoleHandler.canModifyRole(player, id)) {
                        player.sendSystemMessage(Component.nullToEmpty("You do not have permission to give this role!"));
                        return;
                    }
                }
                RoleHandler.changeRoles(player, message.target, message.ids);
            };
        }
    }
}
