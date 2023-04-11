package earth.terrarium.prometheus.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.network.messages.client.CommandPermissionsPacket;
import earth.terrarium.prometheus.common.network.messages.server.*;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Prometheus.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, AddLocationPacket.ID, AddLocationPacket.HANDLER, AddLocationPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, AddRolePacket.ID, AddRolePacket.HANDLER, AddRolePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ChangeRolesPacket.ID, ChangeRolesPacket.HANDLER, ChangeRolesPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SaveRolePacket.ID, SaveRolePacket.HANDLER, SaveRolePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, MemberRolesPacket.ID, MemberRolesPacket.HANDLER, MemberRolesPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, CommandPermissionsPacket.ID, CommandPermissionsPacket.HANDLER, CommandPermissionsPacket.class);
    }
}
