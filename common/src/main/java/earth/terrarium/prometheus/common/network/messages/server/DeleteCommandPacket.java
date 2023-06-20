package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommandHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public record DeleteCommandPacket(String command) implements Packet<DeleteCommandPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "delete_command");
    public static final PacketHandler<DeleteCommandPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<DeleteCommandPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<DeleteCommandPacket> {

        @Override
        public void encode(DeleteCommandPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.command);
        }

        @Override
        public DeleteCommandPacket decode(FriendlyByteBuf buffer) {
            return new DeleteCommandPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(DeleteCommandPacket message) {
            return (player, level) -> {
                if (level instanceof ServerLevel serverLevel && player.hasPermissions(4)) {
                    DynamicCommandHandler.removeCommand(serverLevel, message.command);
                }
            };
        }
    }
}
