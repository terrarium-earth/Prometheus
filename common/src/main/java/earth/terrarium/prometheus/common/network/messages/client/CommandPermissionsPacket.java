package earth.terrarium.prometheus.common.network.messages.client;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record CommandPermissionsPacket(List<String> permissions) implements Packet<CommandPermissionsPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "command_permissions");
    public static final PacketHandler<CommandPermissionsPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<CommandPermissionsPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<CommandPermissionsPacket> {

        @Override
        public void encode(CommandPermissionsPacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(message.permissions, FriendlyByteBuf::writeUtf);
        }

        @Override
        public CommandPermissionsPacket decode(FriendlyByteBuf buffer) {
            return new CommandPermissionsPacket(buffer.readList(FriendlyByteBuf::readUtf));
        }

        @Override
        public PacketContext handle(CommandPermissionsPacket message) {
            return (player, level) -> {
                CommandPermissionHandler.COMMAND_PERMS.clear();
                CommandPermissionHandler.COMMAND_PERMS.addAll(message.permissions);
            };
        }
    }
}
