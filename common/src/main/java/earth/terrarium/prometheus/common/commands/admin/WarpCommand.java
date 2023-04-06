package earth.terrarium.prometheus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.prometheus.common.handlers.WarpHandler;
import earth.terrarium.prometheus.common.menus.location.Location;
import earth.terrarium.prometheus.common.menus.location.LocationMenu;
import earth.terrarium.prometheus.common.menus.location.LocationType;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;

public class WarpCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGESTION_PROVIDER = (context, builder) -> {
        SharedSuggestionProvider.suggest(WarpHandler.getWarps(context.getSource().getPlayerOrException()), builder);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("warps")
                .requires(source -> source.hasPermission(2))
                .then(add())
                .then(remove())
                .then(list())
                .executes(context -> {
                    WarpCommand.openWarpMenu(context.getSource().getPlayerOrException());
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("warp")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests(SUGGESTION_PROVIDER)
                        .executes(context -> {
                            WarpHandler.teleport(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"));
                            return 1;
                        })
                )
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> add() {
        return Commands.literal("add")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(context -> {
                            WarpHandler.add(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"));
                            return 1;
                        })

                );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> remove() {
        return Commands.literal("remove")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests(SUGGESTION_PROVIDER)
                        .executes(context -> {
                            WarpHandler.remove(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"));
                            return 1;
                        })
                );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> list() {
        return Commands.literal("list")
                .executes(context -> {
                    context.getSource().sendSuccess(Component.literal("Warps:"), false);
                    WarpHandler.getWarps(context.getSource().getPlayerOrException())
                            .stream()
                            .map(WarpCommand::createListEntry)
                            .forEach(msg -> context.getSource().sendSuccess(msg, false));
                    return 1;
                });
    }

    private static Component createListEntry(String name) {
        return Component.literal(" - " + name).setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Component.literal("Click to warp to " + name)
        )).withClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/warp " + name
        )));
    }

    public static void openWarpMenu(ServerPlayer player) {
        Map<String, GlobalPos> homes = WarpHandler.getWarpsMap(player);
        List<Location> locations = homes.entrySet()
                .stream()
                .map(entry -> new Location(entry.getKey(), entry.getValue()))
                .toList();

        //TODO Change to a permission
        int maxAmount = player.hasPermissions(2) ? Integer.MAX_VALUE : -1;

        ModUtils.openMenu(
                player,
                (i, inventory, playerx) -> new LocationMenu(i, LocationType.WARP, maxAmount, locations),
                Component.translatable("prometheus.locations.warp"),
                buf -> LocationMenu.write(buf, LocationType.WARP, maxAmount, locations)
        );
    }
}
