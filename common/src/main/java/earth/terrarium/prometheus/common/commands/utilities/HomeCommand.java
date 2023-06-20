package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.HomeHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

public class HomeCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_HOMES = (context, builder) -> {
        SharedSuggestionProvider.suggest(HomeHandler.getHomes(context.getSource().getPlayerOrException()).keySet(), builder);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("homes")
            .then(add())
            .then(remove())
            .then(list())
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
                context.getSource().sendSuccess(() -> ConstantComponents.HOMES_COMMAND_TITLE, false);
                HomeHandler.getHomes(context.getSource().getPlayerOrException())
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
            Component.translatable("prometheus.locations.home.to", name)
        )).withClickEvent(new ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/home " + name
        )));
    }
}
