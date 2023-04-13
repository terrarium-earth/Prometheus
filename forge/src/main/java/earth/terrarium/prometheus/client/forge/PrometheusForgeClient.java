package earth.terrarium.prometheus.client.forge;

import earth.terrarium.prometheus.client.handlers.NotificationHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

public class PrometheusForgeClient {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(PrometheusForgeClient::onClientMessage);
    }

    public static void onClientMessage(ClientChatReceivedEvent.Player event) {
        NotificationHandler.onChatMessage(event.getMessage(), event.getSender(), event.getBoundChatType());
    }
}
