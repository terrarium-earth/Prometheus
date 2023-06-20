package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.roles.adding.MemberRolesScreen;
import earth.terrarium.prometheus.common.menus.content.MemberRolesContent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record OpenMemberRolesScreenPacket(MemberRolesContent content) implements Packet<OpenMemberRolesScreenPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_member_roles_screen");
    public static final PacketHandler<OpenMemberRolesScreenPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenMemberRolesScreenPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenMemberRolesScreenPacket> {

        @Override
        public void encode(OpenMemberRolesScreenPacket message, FriendlyByteBuf buffer) {
            message.content().write(buffer);
        }

        @Override
        public OpenMemberRolesScreenPacket decode(FriendlyByteBuf buffer) {
            return new OpenMemberRolesScreenPacket(MemberRolesContent.read(buffer));
        }

        @Override
        public PacketContext handle(OpenMemberRolesScreenPacket message) {
            return (player, level) -> Minecraft.getInstance().setScreen(new MemberRolesScreen(message.content()));
        }
    }
}
