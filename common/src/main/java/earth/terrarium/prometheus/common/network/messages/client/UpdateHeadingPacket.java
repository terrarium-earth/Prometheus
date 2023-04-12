package earth.terrarium.prometheus.common.network.messages.client;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.utils.ClientListenerHook;
import earth.terrarium.prometheus.common.handlers.heading.Heading;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public record UpdateHeadingPacket(List<Pair<UUID, Heading>> headings) implements Packet<UpdateHeadingPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "update_heading");
    public static final PacketHandler<UpdateHeadingPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<UpdateHeadingPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<UpdateHeadingPacket> {

        @Override
        public void encode(UpdateHeadingPacket message, FriendlyByteBuf buffer) {
            buffer.writeCollection(message.headings, (buf, pair) -> {
                buf.writeUUID(pair.getFirst());
                buf.writeEnum(pair.getSecond());
            });
        }

        @Override
        public UpdateHeadingPacket decode(FriendlyByteBuf buffer) {
            return new UpdateHeadingPacket(buffer.readList(buf -> Pair.of(buf.readUUID(), buf.readEnum(Heading.class))));
        }

        @Override
        public PacketContext handle(UpdateHeadingPacket message) {
            return (player, level) -> {
                ClientPacketListener listener = Minecraft.getInstance().getConnection();
                if (listener instanceof ClientListenerHook hook) {
                    message.headings.forEach(pair -> hook.prometheus$setHeading(pair.getFirst(), pair.getSecond()));
                }
            };
        }
    }
}
