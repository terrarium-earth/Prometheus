package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommand;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommandException;
import earth.terrarium.prometheus.common.handlers.commands.DynamicCommandHandler;
import net.minecraft.Optionull;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.List;

public class RunCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_IDS = (context, builder) ->
        SharedSuggestionProvider.suggest(DynamicCommandHandler.getCommands(context.getSource().getLevel()), builder);

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_NAMES = (context, builder) ->
        SharedSuggestionProvider.suggest(context.getSource().getOnlinePlayerNames(), builder);


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("run")
            .then(Commands.argument("id", StringArgumentType.word()).suggests(SUGGEST_IDS)
                .then(Commands.argument("args", StringArgumentType.greedyString()).suggests(SUGGEST_NAMES)
                    .executes(context -> runArgCommand(dispatcher, context)))
                .executes(context -> runArgLessCommand(dispatcher, context))));
    }

    private static int runArgCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        if (checkPermissions(context.getSource(), id)) {
            context.getSource().sendFailure(ConstantComponents.NO_PERMISSION);
            return 0;
        }
        String username = Optionull.mapOrDefault(context.getSource().getPlayer(), player -> player.getGameProfile().getName(), "Unknown");
        String[] args = StringArgumentType.getString(context, "args").split(" ");
        List<String> output = DynamicCommandHandler.getCommand(context.getSource().getLevel(), id);
        if (output.isEmpty()) return 0;
        try {
            return DynamicCommand.execute(dispatcher, context.getSource(), username, args, output);
        } catch (DynamicCommandException e) {
            throw new CommandRuntimeException(Component.literal(e.getMessage()));
        }
    }

    private static int runArgLessCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "id");
        if (checkPermissions(context.getSource(), id)) {
            context.getSource().sendFailure(ConstantComponents.NO_PERMISSION);
            return 0;
        }
        String username = Optionull.mapOrDefault(context.getSource().getPlayer(), player -> player.getGameProfile().getName(), "Unknown");
        List<String> output = DynamicCommandHandler.getCommand(context.getSource().getLevel(), id);
        if (output.isEmpty()) return 0;
        try {
            return DynamicCommand.execute(dispatcher, context.getSource(), username, new String[0], output);
        } catch (DynamicCommandException e) {
            throw new CommandRuntimeException(Component.literal(e.getMessage()));
        }
    }

    private static boolean checkPermissions(CommandSourceStack source, String id) {
        if (source.hasPermission(2)) return false;
        TriState state = source.isPlayer() ? PermissionApi.API.getPermission(source.getPlayer(), "command.run." + id) : TriState.TRUE;
        return !state.isTrue();
    }
}
