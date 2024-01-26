package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.commands.EditCommandScreen;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public record ClientboundOpenCommandScreenPacket(
    String command, List<String> content, Set<String> commands
) implements Packet<ClientboundOpenCommandScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenCommandScreenPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundOpenCommandScreenPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ClientboundOpenCommandScreenPacket> implements ClientboundPacketType<ClientboundOpenCommandScreenPacket> {

        public Type() {
            super(
                ClientboundOpenCommandScreenPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "open_command_screen"),
                ObjectByteCodec.create(
                    ByteCodec.STRING.fieldOf(ClientboundOpenCommandScreenPacket::command),
                    ByteCodec.STRING.listOf().fieldOf(ClientboundOpenCommandScreenPacket::content),
                    ByteCodec.STRING.setOf().fieldOf(ClientboundOpenCommandScreenPacket::commands),
                    ClientboundOpenCommandScreenPacket::new
                )
            );
        }

        @Override
        public Runnable handle(ClientboundOpenCommandScreenPacket message) {
            return () -> EditCommandScreen.open(message.commands(), message.content(), message.command());
        }
    }
}
