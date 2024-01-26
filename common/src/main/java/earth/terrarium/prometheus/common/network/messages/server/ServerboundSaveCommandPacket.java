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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Consumer;

public record ServerboundSaveCommandPacket(String id, List<String> lines) implements Packet<ServerboundSaveCommandPacket> {

    public static final ServerboundPacketType<ServerboundSaveCommandPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundSaveCommandPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundSaveCommandPacket> implements ServerboundPacketType<ServerboundSaveCommandPacket> {

        public Type() {
            super(
                ServerboundSaveCommandPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "save_command"),
                ObjectByteCodec.create(
                    ByteCodec.STRING.fieldOf(ServerboundSaveCommandPacket::id),
                    ByteCodec.STRING.listOf().fieldOf(ServerboundSaveCommandPacket::lines),
                    ServerboundSaveCommandPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundSaveCommandPacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(4)) {
                    DynamicCommandHandler.putCommand(serverPlayer.serverLevel(), message.id, message.lines);
                }
            };
        }
    }
}
