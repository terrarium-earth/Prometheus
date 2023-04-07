package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.prometheus.common.handlers.heading.Heading;
import earth.terrarium.prometheus.common.handlers.heading.HeadingHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class HeadingCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_HEADINGS = (context, builder) -> {
        List<Heading> headings = Heading.VALUES.stream().filter(heading -> heading.hasPermission(context.getSource().getPlayer())).toList();
        SharedSuggestionProvider.suggest(headings, builder,
                heading -> heading.name().charAt(0) + heading.name().substring(1).toLowerCase(),
                Heading::getDisplayName);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("heading")
                .then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_HEADINGS)
                        .executes(context -> {
                            Heading heading = Heading.fromCommand(context);
                            if (heading != null) {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                if (HeadingHandler.set(player, heading)) {
                                    player.sendSystemMessage(Component.translatable("prometheus.heading.set", heading.getDisplayName()));
                                } else {
                                    player.sendSystemMessage(Component.translatable("prometheus.heading.invalid_permission", heading.getDisplayName()));
                                }
                            } else {
                                context.getSource().sendFailure(Component.translatable("prometheus.heading.invalid", StringArgumentType.getString(context, "name")));
                            }
                            return 1;
                        })));
    }
}
