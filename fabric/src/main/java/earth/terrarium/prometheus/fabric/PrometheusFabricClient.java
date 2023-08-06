package earth.terrarium.prometheus.fabric;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import earth.terrarium.prometheus.client.PrometheusClient;
import earth.terrarium.prometheus.client.commands.ModClientCommands;
import earth.terrarium.prometheus.client.handlers.NotificationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            ModClientCommands.register(dispatcher, new FabricCommandBuilder())
        );
        PrometheusClient.KEYS.forEach(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(client -> PrometheusClient.clientTick());
    }

    private static class FabricCommandBuilder implements ModClientCommands.ClientCommandBuilder<FabricClientCommandSource> {

        @Override
        public LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
            return ClientCommandManager.literal(name);
        }

        @Override
        public <I> RequiredArgumentBuilder<FabricClientCommandSource, I> argument(String name, ArgumentType<I> type) {
            return ClientCommandManager.argument(name, type);
        }

        @Override
        public boolean hasPermission(FabricClientCommandSource source, int permissionLevel) {
            return source.hasPermission(permissionLevel);
        }

        @Override
        public void sendFailure(FabricClientCommandSource source, Component component) {
            source.sendError(component);
        }
    }
}
