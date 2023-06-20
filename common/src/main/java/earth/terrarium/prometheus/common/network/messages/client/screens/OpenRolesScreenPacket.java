package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.roles.RolesScreen;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record OpenRolesScreenPacket(RolesContent content) implements Packet<OpenRolesScreenPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_roles_screen");
    public static final PacketHandler<OpenRolesScreenPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenRolesScreenPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenRolesScreenPacket> {

        @Override
        public void encode(OpenRolesScreenPacket message, FriendlyByteBuf buffer) {
            message.content().write(buffer);
        }

        @Override
        public OpenRolesScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenRolesScreenPacket(RolesContent.read(buffer));
        }

        @Override
        public PacketContext handle(OpenRolesScreenPacket message) {
            return (player, level) -> Minecraft.getInstance().setScreen(new RolesScreen(message.content()));
        }
    }
}
