package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.roles.editing.RoleEditScreen;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record OpenRoleScreenPacket(RoleEditContent content) implements Packet<OpenRoleScreenPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_role_screen");
    public static final PacketHandler<OpenRoleScreenPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenRoleScreenPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenRoleScreenPacket> {

        @Override
        public void encode(OpenRoleScreenPacket message, FriendlyByteBuf buffer) {
            message.content().write(buffer);
        }

        @Override
        public OpenRoleScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenRoleScreenPacket(RoleEditContent.read(buffer));
        }

        @Override
        public PacketContext handle(OpenRoleScreenPacket message) {
            return (player, level) -> Minecraft.getInstance().setScreen(new RoleEditScreen(message.content()));
        }
    }
}
