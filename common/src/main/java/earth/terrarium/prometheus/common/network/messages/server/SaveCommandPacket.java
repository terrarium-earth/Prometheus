package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommandHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public record SaveCommandPacket(String id, List<String> lines) implements Packet<SaveCommandPacket> {

    public static final PacketHandler<SaveCommandPacket> HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "save_command");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SaveCommandPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<SaveCommandPacket> {

        @Override
        public void encode(SaveCommandPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id);
            buffer.writeCollection(message.lines, FriendlyByteBuf::writeUtf);
        }

        @Override
        public SaveCommandPacket decode(FriendlyByteBuf buffer) {
            return new SaveCommandPacket(buffer.readUtf(), buffer.readList(FriendlyByteBuf::readUtf));
        }

        @Override
        public PacketContext handle(SaveCommandPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(4)) {
                    DynamicCommandHandler.putCommand(serverPlayer.getLevel(), message.id, message.lines);
                }
            };
        }
    }
}
