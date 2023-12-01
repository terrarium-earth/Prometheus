package earth.terrarium.prometheus.neoforge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import earth.terrarium.prometheus.client.PrometheusClient;
import earth.terrarium.prometheus.client.commands.ModClientCommands;
import earth.terrarium.prometheus.client.handlers.NotificationHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

public class PrometheusNeoForgeClient {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PrometheusNeoForgeClient::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PrometheusNeoForgeClient::onRegisterKeyBindings);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForgeClient::onClientMessage);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForgeClient::onRegisterClientCommands);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForgeClient::onClientTick);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
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

    private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ModClientCommands.register(event.getDispatcher(), new ForgeCommandBuilder());
    }

    private static class ForgeCommandBuilder implements ModClientCommands.ClientCommandBuilder<CommandSourceStack> {

        @Override
        public LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
            return Commands.literal(name);
        }

        @Override
        public <I> RequiredArgumentBuilder<CommandSourceStack, I> argument(String name, ArgumentType<I> type) {
            return Commands.argument(name, type);
        }

        @Override
        public boolean hasPermission(CommandSourceStack source, int permissionLevel) {
            return source.hasPermission(permissionLevel);
        }

        @Override
        public void sendFailure(CommandSourceStack source, Component component) {
            source.sendFailure(component);
        }
    }
}
