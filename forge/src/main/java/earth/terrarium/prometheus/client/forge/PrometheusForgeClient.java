package earth.terrarium.prometheus.client.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import earth.terrarium.prometheus.client.PrometheusClient;
import earth.terrarium.prometheus.client.commands.ModClientCommands;
import earth.terrarium.prometheus.client.handlers.NotificationHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class PrometheusForgeClient {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PrometheusForgeClient::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForgeClient::onClientMessage);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForgeClient::onRegisterClientCommands);
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
