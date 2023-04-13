package earth.terrarium.prometheus.client.fabric;

import com.mojang.authlib.GameProfile;
import earth.terrarium.prometheus.client.PrometheusClient;
import earth.terrarium.prometheus.client.handlers.NotificationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class PrometheusFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PrometheusClient.init();
        ClientReceiveMessageEvents.CHAT.register((Component message, @Nullable PlayerChatMessage signedMessage, @Nullable GameProfile sender, ChatType.Bound params, Instant receptionTimestamp) ->
            NotificationHandler.onChatMessage(message, sender == null ? null : sender.getId(), params)
        );
    }
}
