package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import earth.terrarium.prometheus.common.handlers.nickname.Nickname;
import earth.terrarium.prometheus.common.handlers.nickname.NicknameHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class NicknameCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nickname")
            .then(Commands.literal("remove").executes(context -> {
                NicknameHandler.remove(context.getSource().getPlayerOrException());
                return 1;
            }))
            .then(Commands.literal("set").then(Commands.argument("nickname", StringArgumentType.greedyString())
                .executes(context -> {
                    NicknameHandler.set(context.getSource().getPlayerOrException(), Component.literal(StringArgumentType.getString(context, "nickname")));
                    return 1;
                })
            )));
        dispatcher.register(Commands.literal("nicknames")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("set")
                .then(Commands.argument("nickname", StringArgumentType.string())
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            NicknameHandler.set(EntityArgument.getPlayer(context, "player"), Component.literal(StringArgumentType.getString(context, "nickname")));
                            return 1;
                        })
                    )
                ))
            .then(Commands.literal("remove")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        NicknameHandler.remove(EntityArgument.getPlayer(context, "player"));
                        return 1;
                    })
                )
            )
            .then(Commands.literal("list")
                .executes(context -> {
                    context.getSource().sendSuccess(() -> Component.empty().append("Nicknames:"), false);
                    NicknameHandler.names(context.getSource().getLevel())
                        .values()
                        .stream().map(NicknameCommand::getNicknameEntry)
                        .forEach(component -> context.getSource().sendSuccess(() -> component, false));
                    return 1;
                })
            ));
    }

    private static Component getNicknameEntry(Nickname nickname) {
        return Component.empty().append(nickname.name()).append(" - ").append(nickname.component());
    }
}
