package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import earth.terrarium.prometheus.common.commands.ModCommands;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.locations.HomeHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenLocationScreenPacket;
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
        dispatcher.register(Commands.literal("homes")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                ModCommands.checkPacket(player, ClientboundOpenLocationScreenPacket.TYPE);
                ClientboundOpenLocationScreenPacket.openHomes(player);
                return Command.SINGLE_SUCCESS;
            }));

        dispatcher.register(Commands.literal("home")
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
        dispatcher.register(add());
        dispatcher.register(remove());
        dispatcher.register(list());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> add() {
        return Commands.literal("sethome")
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    String homeName = StringArgumentType.getString(context, "name");
                    HomeHandler.add(player, homeName);
                    player.sendSystemMessage(CommonUtils.serverTranslatable("prometheus.locations.home.created", homeName));
                    return 1;
                })
            )
            .executes(context -> {
                HomeHandler.add(context.getSource().getPlayerOrException(), "home");
                return 1;
            });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> remove() {
        return Commands.literal("delhome")
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .suggests(SUGGEST_HOMES)
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    String homeName = StringArgumentType.getString(context, "name");
                    HomeHandler.remove(player, homeName);
                    player.sendSystemMessage(CommonUtils.serverTranslatable("prometheus.locations.home.removed", homeName));
                    return 1;
                })

            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> list() {
        return Commands.literal("listhomes")
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
