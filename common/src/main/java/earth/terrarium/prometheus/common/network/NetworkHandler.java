package earth.terrarium.prometheus.common.network;

import com.teamresourceful.resourcefullib.common.network.Network;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.network.messages.client.ClientboundCommandPermissionsPacket;
import earth.terrarium.prometheus.common.network.messages.client.ClientboundUpdateHeadingPacket;
import earth.terrarium.prometheus.common.network.messages.client.screens.*;
import earth.terrarium.prometheus.common.network.messages.server.*;
import earth.terrarium.prometheus.common.network.messages.server.roles.*;

public class NetworkHandler {

    public static final Network CHANNEL = new Network(Prometheus.id("main"), 1, true);

    public static void init() {
        CHANNEL.register(ServerboundAddLocationPacket.TYPE);
        CHANNEL.register(ServerboundDeleteLocationPacket.TYPE);
        CHANNEL.register(ServerboundAddRolePacket.TYPE);
        CHANNEL.register(ServerboundChangeRolesPacket.TYPE);
        CHANNEL.register(ServerboundSaveRolePacket.TYPE);
        CHANNEL.register(ServerboundMemberRolesPacket.TYPE);
        CHANNEL.register(ServerboundOpenRolePacket.TYPE);
        CHANNEL.register(ServerboundOpenRolesPacket.TYPE);
        CHANNEL.register(ServerboundOpenLocationPacket.TYPE);
        CHANNEL.register(ServerboundOpenMemberRolesPacket.TYPE);
        CHANNEL.register(ServerboundGoHomePacket.TYPE);
        CHANNEL.register(ServerboundGoSpawnPacket.TYPE);

        CHANNEL.register(ClientboundCommandPermissionsPacket.TYPE);
        CHANNEL.register(ClientboundUpdateHeadingPacket.TYPE);
        CHANNEL.register(ClientboundOpenRolesScreenPacket.TYPE);
        CHANNEL.register(ClientboundOpenRoleScreenPacket.TYPE);
        CHANNEL.register(ClientboundOpenLocationScreenPacket.TYPE);
        CHANNEL.register(ClientboundOpenMemberRolesScreenPacket.TYPE);
        CHANNEL.register(ClientboundOpenInvseeScreenPacket.TYPE);
    }
}
