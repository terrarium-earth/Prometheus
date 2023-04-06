package earth.terrarium.prometheus.common.commands.cheating;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class FeedCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("feed")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("players", EntityArgument.players())
                    .executes(context -> {
                        EntityArgument.getPlayers(context, "players").forEach(FeedCommand::feed);
                        return 1;
                    })
                ).executes(context -> {
                    FeedCommand.feed(context.getSource().getEntity());
                    return 1;
                }));
    }

    private static void feed(Entity entity) {
        if (entity instanceof Player player) {
            player.getFoodData().eat(69420, 69420);
        }
    }
}
