package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommandHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record ServerboundDeleteCommandPacket(String command) implements Packet<ServerboundDeleteCommandPacket> {

    public static final ServerboundPacketType<ServerboundDeleteCommandPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundDeleteCommandPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundDeleteCommandPacket> implements ServerboundPacketType<ServerboundDeleteCommandPacket> {

        public Type() {
            super(
                ServerboundDeleteCommandPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "delete_command"),
                ObjectByteCodec.create(
                    ByteCodec.STRING.fieldOf(ServerboundDeleteCommandPacket::command),
                    ServerboundDeleteCommandPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundDeleteCommandPacket message) {
            return player -> {
                if (player.level() instanceof ServerLevel serverLevel && player.hasPermissions(4)) {
                    DynamicCommandHandler.removeCommand(serverLevel, message.command);
                }
            };
        }
    }
}
