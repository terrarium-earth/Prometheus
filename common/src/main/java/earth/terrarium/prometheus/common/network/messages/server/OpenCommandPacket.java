package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommandHandler;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.OpenCommandScreenPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.Collection;

public record OpenCommandPacket(String command) implements Packet<OpenCommandPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_command");
    public static final PacketHandler<OpenCommandPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenCommandPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenCommandPacket> {

        @Override
        public void encode(OpenCommandPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.command);
        }

        @Override
        public OpenCommandPacket decode(FriendlyByteBuf buffer) {
            return new OpenCommandPacket(buffer.readUtf());
        }

        @Override
        public PacketContext handle(OpenCommandPacket message) {
            return (player, level) -> {
                if (level instanceof ServerLevel serverLevel && player.hasPermissions(4)) {
                    Collection<String> commands = DynamicCommandHandler.getCommands(serverLevel);
                    String command = message.command();
                    if (command.isEmpty()) command = commands.stream().findFirst().orElse("");
                    if (command.isEmpty()) command = "example";

                    NetworkHandler.CHANNEL.sendToPlayer(new OpenCommandScreenPacket(
                        command,
                        DynamicCommandHandler.getCommand(serverLevel, command),
                        commands
                    ), player);
                }
            };
        }
    }
}
