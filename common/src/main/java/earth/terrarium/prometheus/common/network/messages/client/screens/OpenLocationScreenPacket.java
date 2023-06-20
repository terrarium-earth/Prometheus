package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.location.LocationScreen;
import earth.terrarium.prometheus.common.menus.content.location.LocationContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record OpenLocationScreenPacket(LocationContent content) implements Packet<OpenLocationScreenPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_location_screen");
    public static final PacketHandler<OpenLocationScreenPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenLocationScreenPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenLocationScreenPacket> {

        @Override
        public void encode(OpenLocationScreenPacket message, FriendlyByteBuf buffer) {
            message.content().write(buffer);
        }

        @Override
        public OpenLocationScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenLocationScreenPacket(LocationContent.read(buffer));
        }

        @Override
        public PacketContext handle(OpenLocationScreenPacket message) {
            return (player, level) -> LocationScreen.open(message.content());
        }
    }
}
