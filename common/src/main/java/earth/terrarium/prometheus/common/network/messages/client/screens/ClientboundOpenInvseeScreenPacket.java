package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.InvseeScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record ClientboundOpenInvseeScreenPacket(
    int containerId, int size, UUID player, Component title
) implements Packet<ClientboundOpenInvseeScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenInvseeScreenPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundOpenInvseeScreenPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ClientboundOpenInvseeScreenPacket> implements ClientboundPacketType<ClientboundOpenInvseeScreenPacket> {

        public Type() {
            super(
                ClientboundOpenInvseeScreenPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "open_invsee_screen"),
                ObjectByteCodec.create(
                    ByteCodec.INT.fieldOf(ClientboundOpenInvseeScreenPacket::containerId),
                    ByteCodec.INT.fieldOf(ClientboundOpenInvseeScreenPacket::size),
                    ByteCodec.UUID.fieldOf(ClientboundOpenInvseeScreenPacket::player),
                    ExtraByteCodecs.COMPONENT.fieldOf(ClientboundOpenInvseeScreenPacket::title),
                    ClientboundOpenInvseeScreenPacket::new
                )
            );
        }

        @Override
        public Runnable handle(ClientboundOpenInvseeScreenPacket message) {
            return () -> InvseeScreen.open(message.containerId, message.size, message.player, message.title);
        }
    }
}
