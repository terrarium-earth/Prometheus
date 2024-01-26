package earth.terrarium.prometheus.common.network.messages.client;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissionHandler;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ClientboundCommandPermissionsPacket(
    List<String> permissions) implements Packet<ClientboundCommandPermissionsPacket> {

    public static final ClientboundPacketType<ClientboundCommandPermissionsPacket> TYPE = new Type();

    @Override
    public PacketType<ClientboundCommandPermissionsPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ClientboundCommandPermissionsPacket> implements ClientboundPacketType<ClientboundCommandPermissionsPacket> {

        public Type() {
            super(
                ClientboundCommandPermissionsPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "command_permissions"),
                ObjectByteCodec.create(
                    ByteCodec.STRING.listOf().fieldOf(ClientboundCommandPermissionsPacket::permissions),
                    ClientboundCommandPermissionsPacket::new
                )
            );
        }

        @Override
        public Runnable handle(ClientboundCommandPermissionsPacket message) {
            return () -> {
                CommandPermissionHandler.COMMAND_PERMS.clear();
                CommandPermissionHandler.COMMAND_PERMS.addAll(message.permissions);
            };
        }
    }
}
