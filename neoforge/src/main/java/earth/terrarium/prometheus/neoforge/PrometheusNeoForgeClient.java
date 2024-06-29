package earth.terrarium.prometheus.neoforge;

import earth.terrarium.prometheus.client.PrometheusClient;
import earth.terrarium.prometheus.client.handlers.NotificationHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

public class PrometheusNeoForgeClient {

    public static void init(IEventBus bus) {
        bus.addListener(PrometheusNeoForgeClient::onClientSetup);
        bus.addListener(PrometheusNeoForgeClient::onRegisterKeyBindings);
        bus.addListener(PrometheusNeoForgeClient::onClientReloadListeners);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForgeClient::onClientMessage);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForgeClient::onClientTick);
    }

    private static void onClientTick(ClientTickEvent event) {
        PrometheusClient.clientTick();
    }

    private static void onRegisterKeyBindings(RegisterKeyMappingsEvent event) {
        PrometheusClient.KEYS.forEach(event::register);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(PrometheusClient::init);
    }

    private static void onClientMessage(ClientChatReceivedEvent.Player event) {
        NotificationHandler.onChatMessage(event.getMessage(), event.getSender(), event.getBoundChatType());
    }

    public static void onClientReloadListeners(RegisterClientReloadListenersEvent event) {
        PrometheusClient.initReloadListeners((id, listener) -> event.registerReloadListener(listener));
    }
}
