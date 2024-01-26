package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommandHandler;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenCommandScreenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.function.Consumer;

public record ServerboundOpenCommandPacket(String command) implements Packet<ServerboundOpenCommandPacket> {

    public static final ServerboundPacketType<ServerboundOpenCommandPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundOpenCommandPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundOpenCommandPacket> implements ServerboundPacketType<ServerboundOpenCommandPacket> {

        public Type() {
            super(
                ServerboundOpenCommandPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "open_command"),
                ObjectByteCodec.create(
                    ByteCodec.STRING.fieldOf(ServerboundOpenCommandPacket::command),
                    ServerboundOpenCommandPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundOpenCommandPacket message) {
            return player -> {
                if (player.level() instanceof ServerLevel serverLevel && player.hasPermissions(4)) {
                    Set<String> commands = DynamicCommandHandler.getCommands(serverLevel);
                    String command = message.command();
                    if (command.isEmpty()) command = commands.stream().findFirst().orElse("");
                    if (command.isEmpty()) command = "example";

                    NetworkHandler.CHANNEL.sendToPlayer(new ClientboundOpenCommandScreenPacket(
                        command,
                        DynamicCommandHandler.getCommand(serverLevel, command),
                        commands
                    ), player);
                }
            };
        }
    }
}
