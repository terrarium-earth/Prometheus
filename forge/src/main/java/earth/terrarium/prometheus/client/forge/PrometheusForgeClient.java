package earth.terrarium.prometheus.client.forge;

import earth.terrarium.prometheus.client.PrometheusClient;
import earth.terrarium.prometheus.client.handlers.NotificationHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class PrometheusForgeClient {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PrometheusForgeClient::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForgeClient::onClientMessage);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(PrometheusClient::init);
    }

    private static void onClientMessage(ClientChatReceivedEvent.Player event) {
        NotificationHandler.onChatMessage(event.getMessage(), event.getSender(), event.getBoundChatType());
    }
}
