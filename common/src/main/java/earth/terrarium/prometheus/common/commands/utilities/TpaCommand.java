package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import earth.terrarium.prometheus.common.handlers.tpa.TpaHandler;
import earth.terrarium.prometheus.common.handlers.tpa.TpaRequest;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;

public class TpaCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tpa")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            tpa(context, TpaRequest.Direction.TO);
                            return 1;
                        })
                ));
        dispatcher.register(Commands.literal("tpahere")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            tpa(context, TpaRequest.Direction.FROM);
                            return 1;
                        })
                ));

        dispatcher.register(Commands.literal("tpaccept")
                .then(Commands.argument("id", UuidArgument.uuid())
                        .executes(context -> {
                            TpaHandler.acceptRequest(context.getSource().getPlayerOrException(), UuidArgument.getUuid(context, "id"));
                            return 1;
                        })
                ));

        dispatcher.register(Commands.literal("tpdeny")
                .then(Commands.argument("id", UuidArgument.uuid())
                        .executes(context -> {
                            TpaHandler.denyRequest(context.getSource().getPlayerOrException(), UuidArgument.getUuid(context, "id"));
                            return 1;
                        })
                ));
    }

    private static void tpa(CommandContext<CommandSourceStack> context, TpaRequest.Direction direction) throws CommandSyntaxException {
        TpaHandler.sendRequest(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "player"), direction);
    }
}
