package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.common.handlers.heading.Heading;
import earth.terrarium.prometheus.common.handlers.heading.HeadingHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
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
            .then(Commands.argument("name", StringArgumentType.string())
                .suggests(SUGGEST_HEADINGS)
                .executes(context -> {
                    Heading heading = Heading.fromCommand(context);
                    if (heading != null) {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        Heading currentHeading = HeadingHandler.get(player);
                        if (HeadingHandler.set(player, heading)) {
                            player.sendSystemMessage(CommonUtils.serverTranslatable("prometheus.heading.set", heading.getDisplayName()));
                            if (heading.canBroadcast() && currentHeading != heading) {
                                player.server.getPlayerList().broadcastSystemMessage(
                                    CommonUtils.serverTranslatable("prometheus.heading.broadcast", player.getDisplayName(), heading.getDisplayName()).copy()
                                        .withStyle(style -> style.withColor(heading.getColor()).withBold(true)),
                                    false
                                );
                            }
                        } else {
                            player.sendSystemMessage(CommonUtils.serverTranslatable("prometheus.heading.invalid_permission", heading.getDisplayName()));
                        }
                    } else {
                        context.getSource().sendFailure(CommonUtils.serverTranslatable("prometheus.heading.invalid", StringArgumentType.getString(context, "name")));
                    }
                    return 1;
                })));
    }
}
