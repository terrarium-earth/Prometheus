package earth.terrarium.prometheus.common.commands.admin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import earth.terrarium.prometheus.common.handlers.MuteHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.server.level.ServerLevel;

import java.time.temporal.ChronoUnit;

public class MuteCommand {

    private static final long FIFTY_YEARS = 50L * 365L * 24L * 60L * 60L * 1000L;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mute")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .then(
                                Commands.argument("time", TimeArgument.time()).executes(context -> {
                                    mute(context, IntegerArgumentType.getInteger(context, "time") * 50L);
                                    return 1;
                                })
                        ).executes(context -> {
                            mute(context, FIFTY_YEARS);
                            return 1;
                        })
                )
        );
        dispatcher.register(Commands.literal("unmute")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .executes(context -> {
                            unmute(context);
                            return 1;
                        })
                )
        );
    }


    private static void mute(CommandContext<CommandSourceStack> context, long time) throws CommandSyntaxException {
        final ServerLevel level = context.getSource().getLevel();
        for (GameProfile player : GameProfileArgument.getGameProfiles(context, "player")) {
            MuteHandler.mute(level, player, time, ChronoUnit.MILLIS);
        }
    }

    private static void unmute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final ServerLevel level = context.getSource().getLevel();
        for (GameProfile player : GameProfileArgument.getGameProfiles(context, "player")) {
            MuteHandler.unmute(level, player);
        }
    }
}
