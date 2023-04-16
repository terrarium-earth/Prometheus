package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public class HatCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hat")
            .requires(source -> source.hasPermission(2))
            .executes(context -> {
                HatCommand.hat(context.getSource().getEntity());
                return 1;
            }));
    }

    private static void hat(Entity entity) {
        if (entity instanceof Player player) {
            ModUtils.swapItems(player, EquipmentSlot.HEAD, EquipmentSlot.MAINHAND);
        }
    }
}
