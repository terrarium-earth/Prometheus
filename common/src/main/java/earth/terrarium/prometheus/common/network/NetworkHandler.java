package earth.terrarium.prometheus.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.network.messages.client.CommandPermissionsPacket;
import earth.terrarium.prometheus.common.network.messages.client.UpdateHeadingPacket;
import earth.terrarium.prometheus.common.network.messages.client.screens.*;
import earth.terrarium.prometheus.common.network.messages.server.*;
import earth.terrarium.prometheus.common.network.messages.server.roles.*;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Prometheus.MOD_ID, 1, "main", true);

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, AddLocationPacket.ID, AddLocationPacket.HANDLER, AddLocationPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, DeleteLocationPacket.ID, DeleteLocationPacket.HANDLER, DeleteLocationPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, AddRolePacket.ID, AddRolePacket.HANDLER, AddRolePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ChangeRolesPacket.ID, ChangeRolesPacket.HANDLER, ChangeRolesPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SaveRolePacket.ID, SaveRolePacket.HANDLER, SaveRolePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, MemberRolesPacket.ID, MemberRolesPacket.HANDLER, MemberRolesPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SaveCommandPacket.ID, SaveCommandPacket.HANDLER, SaveCommandPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenCommandPacket.ID, OpenCommandPacket.HANDLER, OpenCommandPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, DeleteCommandPacket.ID, DeleteCommandPacket.HANDLER, DeleteCommandPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenRolePacket.ID, OpenRolePacket.HANDLER, OpenRolePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenRolesPacket.ID, OpenRolesPacket.HANDLER, OpenRolesPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenRolePacket.ID, OpenRolePacket.HANDLER, OpenRolePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenLocationPacket.ID, OpenLocationPacket.HANDLER, OpenLocationPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenMemberRolesPacket.ID, OpenMemberRolesPacket.HANDLER, OpenMemberRolesPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, GoHomePacket.ID, GoHomePacket.HANDLER, GoHomePacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, GoSpawnPacket.ID, GoSpawnPacket.HANDLER, GoSpawnPacket.class);

        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, CommandPermissionsPacket.ID, CommandPermissionsPacket.HANDLER, CommandPermissionsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, UpdateHeadingPacket.ID, UpdateHeadingPacket.HANDLER, UpdateHeadingPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenCommandScreenPacket.ID, OpenCommandScreenPacket.HANDLER, OpenCommandScreenPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenRolesScreenPacket.ID, OpenRolesScreenPacket.HANDLER, OpenRolesScreenPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenRoleScreenPacket.ID, OpenRoleScreenPacket.HANDLER, OpenRoleScreenPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenLocationScreenPacket.ID, OpenLocationScreenPacket.HANDLER, OpenLocationScreenPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenMemberRolesScreenPacket.ID, OpenMemberRolesScreenPacket.HANDLER, OpenMemberRolesScreenPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenInvseeScreenPacket.ID, OpenInvseeScreenPacket.HANDLER, OpenInvseeScreenPacket.class);
    }
}
