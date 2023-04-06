package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.prometheus.common.handlers.HomeHandler;
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

public class HomeCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_HOMES = (context, builder) -> {
        SharedSuggestionProvider.suggest(HomeHandler.getHomes(context.getSource().getPlayerOrException()), builder);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("homes")
                .then(add())
                .then(remove())
                .then(list())
                .executes(context -> {
                    openHomeMenu(context.getSource().getPlayerOrException());
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("home")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests(SUGGEST_HOMES)
                        .executes(context -> {
                            HomeHandler.teleport(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"));
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
                    context.getSource().sendSuccess(Component.literal("Homes:"), false);
                    HomeHandler.getHomes(context.getSource().getPlayerOrException())
                            .stream()
                            .map(HomeCommand::createListEntry)
                            .forEach(msg -> context.getSource().sendSuccess(msg, false));
                    return 1;
                });
    }

    private static Component createListEntry(String name) {
        return Component.literal(" - " + name).setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Component.literal("Click to teleport to " + name)
        )).withClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/home " + name
        )));
    }

    public static void openHomeMenu(ServerPlayer player) {
        Map<String, GlobalPos> homes = HomeHandler.getHomesMap(player);
        List<Location> locations = homes.entrySet()
                .stream()
                .map(entry -> new Location(entry.getKey(), entry.getValue()))
                .toList();

        ModUtils.openMenu(
                player,
                (i, inventory, playerx) -> new LocationMenu(i, LocationType.HOME, HomeHandler.MAX_HOMES, locations),
                Component.translatable("prometheus.locations.home"),
                buf -> LocationMenu.write(buf, LocationType.HOME, HomeHandler.MAX_HOMES, locations)
        );
    }
}
