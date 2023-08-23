package earth.terrarium.prometheus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.locations.WarpHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public class WarpCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGESTION_PROVIDER = (context, builder) -> {
        SharedSuggestionProvider.suggest(LocationsApi.API.getWarps(context.getSource().getServer()).keySet(), builder);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("warp")
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .suggests(SUGGESTION_PROVIDER)
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    return LocationsApi.API.getWarp(player.getServer(), StringArgumentType.getString(context, "name"))
                        .map(location -> {
                            location.teleport(player);
                            return 1;
                        }, error -> {
                            switch (error) {
                                case DOES_NOT_EXIST_WITH_NAME -> context.getSource().sendFailure(ConstantComponents.WARP_DOES_NOT_EXIST);
                                case NO_DIMENSION_FOR_LOCATION -> context.getSource().sendFailure(ConstantComponents.NO_DIMENSION);
                                case NO_LOCATIONS -> context.getSource().sendFailure(ConstantComponents.NO_WARPS);
                            }
                            return 0;
                        });
                })
            )
        );
        dispatcher.register(add());
        dispatcher.register(remove());
        dispatcher.register(list());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> add() {
        return Commands.literal("setwarp")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .executes(context -> {
                    WarpHandler.add(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"));
                    return 1;
                })

            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> remove() {
        return Commands.literal("delwarp")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .suggests(SUGGESTION_PROVIDER)
                .executes(context -> {
                    WarpHandler.remove(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"));
                    return 1;
                })
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> list() {
        return Commands.literal("listwarps")
            .executes(context -> {
                context.getSource().sendSuccess(() -> ConstantComponents.WARPS_COMMAND_TITLE, false);
                LocationsApi.API.getWarps(context.getSource().getServer())
                    .keySet()
                    .stream()
                    .map(WarpCommand::createListEntry)
                    .forEach(msg -> context.getSource().sendSuccess(() -> msg, false));
                return 1;
            });
    }

    private static Component createListEntry(String name) {
        return Component.literal(" - " + name).setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            CommonUtils.serverTranslatable("prometheus.locations.warp.to", name)
        )).withClickEvent(new ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/warp " + name
        )));
    }
}
