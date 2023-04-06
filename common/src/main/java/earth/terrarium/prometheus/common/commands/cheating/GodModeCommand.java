package earth.terrarium.prometheus.common.commands.cheating;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class GodModeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("godmode")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    GodModeCommand.godmode(context.getSource().getEntity());
                    return 1;
                }));
    }

    private static void godmode(Entity entity) {
        if (entity instanceof Player player) {
            player.getAbilities().invulnerable = !player.getAbilities().invulnerable;
            player.onUpdateAbilities();
        }
    }
}
