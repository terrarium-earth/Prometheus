package earth.terrarium.prometheus.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.network.messages.client.CommandPermissionsPacket;
import earth.terrarium.prometheus.common.network.messages.client.UpdateHeadingPacket;
import earth.terrarium.prometheus.common.network.messages.client.screens.*;
import earth.terrarium.prometheus.common.network.messages.server.*;
import earth.terrarium.prometheus.common.network.messages.server.roles.*;
import net.minecraft.network.protocol.PacketFlow;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Prometheus.MOD_ID, 1, "main", true);

    public static void init() {
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, AddLocationPacket.ID, AddLocationPacket.HANDLER, AddLocationPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, DeleteLocationPacket.ID, DeleteLocationPacket.HANDLER, DeleteLocationPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, AddRolePacket.ID, AddRolePacket.HANDLER, AddRolePacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, ChangeRolesPacket.ID, ChangeRolesPacket.HANDLER, ChangeRolesPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, SaveRolePacket.ID, SaveRolePacket.HANDLER, SaveRolePacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, MemberRolesPacket.ID, MemberRolesPacket.HANDLER, MemberRolesPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, SaveCommandPacket.ID, SaveCommandPacket.HANDLER, SaveCommandPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, OpenCommandPacket.ID, OpenCommandPacket.HANDLER, OpenCommandPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, DeleteCommandPacket.ID, DeleteCommandPacket.HANDLER, DeleteCommandPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, OpenRolePacket.ID, OpenRolePacket.HANDLER, OpenRolePacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, OpenRolesPacket.ID, OpenRolesPacket.HANDLER, OpenRolesPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, OpenRolePacket.ID, OpenRolePacket.HANDLER, OpenRolePacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, OpenLocationPacket.ID, OpenLocationPacket.HANDLER, OpenLocationPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, OpenMemberRolesPacket.ID, OpenMemberRolesPacket.HANDLER, OpenMemberRolesPacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, GoHomePacket.ID, GoHomePacket.HANDLER, GoHomePacket.class);
        CHANNEL.registerPacket(PacketFlow.SERVERBOUND, GoSpawnPacket.ID, GoSpawnPacket.HANDLER, GoSpawnPacket.class);

        CHANNEL.registerPacket(PacketFlow.CLIENTBOUND, CommandPermissionsPacket.ID, CommandPermissionsPacket.HANDLER, CommandPermissionsPacket.class);
        CHANNEL.registerPacket(PacketFlow.CLIENTBOUND, UpdateHeadingPacket.ID, UpdateHeadingPacket.HANDLER, UpdateHeadingPacket.class);
        CHANNEL.registerPacket(PacketFlow.CLIENTBOUND, OpenCommandScreenPacket.ID, OpenCommandScreenPacket.HANDLER, OpenCommandScreenPacket.class);
        CHANNEL.registerPacket(PacketFlow.CLIENTBOUND, OpenRolesScreenPacket.ID, OpenRolesScreenPacket.HANDLER, OpenRolesScreenPacket.class);
        CHANNEL.registerPacket(PacketFlow.CLIENTBOUND, OpenRoleScreenPacket.ID, OpenRoleScreenPacket.HANDLER, OpenRoleScreenPacket.class);
        CHANNEL.registerPacket(PacketFlow.CLIENTBOUND, OpenLocationScreenPacket.ID, OpenLocationScreenPacket.HANDLER, OpenLocationScreenPacket.class);
        CHANNEL.registerPacket(PacketFlow.CLIENTBOUND, OpenMemberRolesScreenPacket.ID, OpenMemberRolesScreenPacket.HANDLER, OpenMemberRolesScreenPacket.class);
        CHANNEL.registerPacket(PacketFlow.CLIENTBOUND, OpenInvseeScreenPacket.ID, OpenInvseeScreenPacket.HANDLER, OpenInvseeScreenPacket.class);
    }
}
