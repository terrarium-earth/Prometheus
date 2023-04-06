package earth.terrarium.prometheus.common.commands.cheating;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class FlyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fly")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> {
                            EntityArgument.getPlayers(context, "players").forEach(FlyCommand::fly);
                            return 1;
                        })
                ).executes(context -> {
                    if (context.getSource().getEntity() instanceof Player player) {
                        fly(player);
                    }
                    return 1;
                })
        );
        dispatcher.register(Commands.literal("flyspeed")
                .requires(source -> source.hasPermission(2))
                .then(ModUtils.ofPlayers(Commands.argument("speed", FloatArgumentType.floatArg(0.01f, 2.0f)), (ctx, player) ->
                        FlyCommand.setFlySpeed(player, FloatArgumentType.getFloat(ctx, "speed"))
                ))
        );
    }


    private static void fly(Entity entity) {
        if (entity instanceof Player player) {
            player.getAbilities().flying = !player.getAbilities().flying;
            player.getAbilities().mayfly = !player.getAbilities().mayfly;
            player.onUpdateAbilities();
        }
    }

    private static void setFlySpeed(Entity entity, float speed) {
        if (entity instanceof Player player) {
            player.getAbilities().setFlyingSpeed(speed);
            player.onUpdateAbilities();
        }
    }
}
