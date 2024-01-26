package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.roles.editing.RoleEditScreen;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundOpenRoleScreenPacket(
    RoleEditContent content) implements Packet<ClientboundOpenRoleScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenRoleScreenPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundOpenRoleScreenPacket> type() {
        return TYPE;
    }

    private static class Type implements ClientboundPacketType<ClientboundOpenRoleScreenPacket> {

        @Override
        public Class<ClientboundOpenRoleScreenPacket> type() {
            return ClientboundOpenRoleScreenPacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "open_role_screen");
        }

        @Override
        public void encode(ClientboundOpenRoleScreenPacket message, FriendlyByteBuf buffer) {
            message.content().write(buffer);
        }

        @Override
        public ClientboundOpenRoleScreenPacket decode(FriendlyByteBuf buffer) {
            return new ClientboundOpenRoleScreenPacket(RoleEditContent.read(buffer));
        }

        @Override
        public Runnable handle(ClientboundOpenRoleScreenPacket message) {
            return () -> RoleEditScreen.open(message.content());
        }
    }
}
