package earth.terrarium.prometheus.client.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.OpenCommandPacket;
import earth.terrarium.prometheus.common.network.messages.server.OpenLocationPacket;
import earth.terrarium.prometheus.common.network.messages.server.OpenMemberRolesPacket;
import earth.terrarium.prometheus.common.network.messages.server.roles.OpenRolesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

public class ModClientCommands {


    public static <T> void register(CommandDispatcher<T> dispatcher, ClientCommandBuilder<T> builder) {
        SuggestionProvider<T> playerSuggester = (context, provider) -> {
            if (Minecraft.getInstance().getConnection() == null) return provider.buildFuture();
            Minecraft.getInstance().getConnection()
                .getOnlinePlayers()
                .stream()
                .map(PlayerInfo::getProfile)
                .map(GameProfile::getName)
                .forEach(provider::suggest);
            return provider.buildFuture();
        };


        dispatcher.register(builder.literal("runs").executes(context -> {
            NetworkHandler.CHANNEL.sendToServer(new OpenCommandPacket(""));
            return 1;
        }));
        dispatcher.register(builder.literal("roles")
            .requires(source -> builder.hasPermission(source, 2))
            .executes(context -> {
                NetworkHandler.CHANNEL.sendToServer(new OpenRolesPacket());
                return 1;
            }));
        dispatcher.register(builder.literal("warps")
            .executes(context -> {
                NetworkHandler.CHANNEL.sendToServer(new OpenLocationPacket(LocationType.WARP));
                return 1;
            }));
        dispatcher.register(builder.literal("homes")
            .executes(context -> {
                NetworkHandler.CHANNEL.sendToServer(new OpenLocationPacket(LocationType.HOME));
                return 1;
            }));
        dispatcher.register(builder.literal("member")
            .requires(source -> builder.hasPermission(source, 2))
            .then(builder.argument("player", StringArgumentType.word()).suggests(playerSuggester)
                .executes(context -> {
                    if (Minecraft.getInstance().getConnection() == null) return 0;
                    PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(StringArgumentType.getString(context, "player"));
                    if (info == null) {
                        builder.sendFailure(context.getSource(), ConstantComponents.MEMBER_ERROR);
                        return 0;
                    }
                    GameProfile profile = info.getProfile();
                    NetworkHandler.CHANNEL.sendToServer(new OpenMemberRolesPacket(profile.getId()));
                    return 1;
                })));
    }

    public interface ClientCommandBuilder<T> {

        LiteralArgumentBuilder<T> literal(String name);

        <I> RequiredArgumentBuilder<T, I> argument(String name, ArgumentType<I> type);

        boolean hasPermission(T source, int permissionLevel);

        void sendFailure(T source, Component component);
    }
}
