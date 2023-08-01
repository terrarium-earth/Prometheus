package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.locations.HomeHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public class HomeCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_HOMES = (context, builder) -> {
        SharedSuggestionProvider.suggest(LocationsApi.API.getHomes(context.getSource().getPlayerOrException()).keySet(), builder);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home")
            .then(add())
            .then(remove())
            .then(list())
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .suggests(SUGGEST_HOMES)
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    return LocationsApi.API.getHome(player, StringArgumentType.getString(context, "name"))
                        .map(location -> {
                            location.teleport(player);
                            return 1;
                        }, error -> {
                            switch (error) {
                                case DOES_NOT_EXIST_WITH_NAME -> context.getSource().sendFailure(ConstantComponents.HOME_DOES_NOT_EXIST);
                                case NO_DIMENSION_FOR_LOCATION -> context.getSource().sendFailure(ConstantComponents.NO_DIMENSION);
                                case NO_LOCATIONS -> context.getSource().sendFailure(ConstantComponents.NO_HOMES);
                            }
                            return 0;
                        });
                })
            ).executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                if (!HomeHandler.teleport(player)) {
                    player.sendSystemMessage(ConstantComponents.MULTIPLE_HOMES);
                }
                return 1;
            })
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> add() {
        return Commands.literal("add")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .executes(context -> {
                    HomeHandler.add(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"));
                    return 1;
                })

            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> remove() {
        return Commands.literal("remove")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .suggests(SUGGEST_HOMES)
                .executes(context -> {
                    HomeHandler.remove(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"));
                    return 1;
                })

            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> list() {
        return Commands.literal("list")
            .executes(context -> {
                context.getSource().sendSuccess(() -> ConstantComponents.HOMES_COMMAND_TITLE, false);
                LocationsApi.API.getHomes(context.getSource().getPlayerOrException())
                    .keySet()
                    .stream()
                    .map(HomeCommand::createListEntry)
                    .forEach(msg -> context.getSource().sendSuccess(() -> msg, false));
                return 1;
            });
    }

    private static Component createListEntry(String name) {
        return Component.literal(" - " + name).setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            CommonUtils.serverTranslatable("prometheus.locations.home.to", name)
        )).withClickEvent(new ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/home " + name
        )));
    }
}
