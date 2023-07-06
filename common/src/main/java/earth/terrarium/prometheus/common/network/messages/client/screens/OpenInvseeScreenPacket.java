package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.InvseeScreen;
import earth.terrarium.prometheus.common.menus.InvseeMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;

import java.util.*;

public record OpenInvseeScreenPacket(
    int containerId, int size, UUID player, Component title
) implements Packet<OpenInvseeScreenPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_invsee_screen");
    public static final PacketHandler<OpenInvseeScreenPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenInvseeScreenPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenInvseeScreenPacket> {

        @Override
        public void encode(OpenInvseeScreenPacket message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.containerId);
            buffer.writeVarInt(message.size);
            buffer.writeUUID(message.player);
            buffer.writeComponent(message.title);
        }

        @Override
        public OpenInvseeScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenInvseeScreenPacket(buffer.readVarInt(), buffer.readVarInt(), buffer.readUUID(), buffer.readComponent());
        }

        @Override
        public PacketContext handle(OpenInvseeScreenPacket message) {
            return (player, level) -> {
                SimpleContainer container = new SimpleContainer(message.size());
                InvseeMenu menu = new InvseeMenu(message.containerId(), player.getInventory(), player, container, message.player());
                InvseeScreen.open(menu, message.title());
            };
        }
    }
}
