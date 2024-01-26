package earth.terrarium.prometheus.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.ServerboundOpenCommandPacket;
import earth.terrarium.prometheus.common.network.messages.server.ServerboundOpenLocationPacket;
import net.minecraft.network.chat.Component;

public class ModClientCommands {

    public static <T> void register(CommandDispatcher<T> dispatcher, ClientCommandBuilder<T> builder) {
        dispatcher.register(builder.literal("runs").executes(context -> {
            NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenCommandPacket(""));
            return 1;
        }));
        dispatcher.register(builder.literal("warps")
            .executes(context -> {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenLocationPacket(LocationType.WARP));
                return 1;
            }));
        dispatcher.register(builder.literal("homes")
            .executes(context -> {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenLocationPacket(LocationType.HOME));
                return 1;
            }));
    }

    public interface ClientCommandBuilder<T> {

        LiteralArgumentBuilder<T> literal(String name);

        <I> RequiredArgumentBuilder<T, I> argument(String name, ArgumentType<I> type);

        boolean hasPermission(T source, int permissionLevel);

        void sendFailure(T source, Component component);
    }
}
