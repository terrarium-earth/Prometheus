package earth.terrarium.prometheus.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.network.messages.server.AddLocationPacket;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Prometheus.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, AddLocationPacket.ID, AddLocationPacket.HANDLER, AddLocationPacket.class);
    }
}
