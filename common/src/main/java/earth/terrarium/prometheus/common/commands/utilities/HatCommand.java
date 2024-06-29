package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.EquipmentSlot;

public class HatCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hat")
            .requires(source -> source.hasPermission(2))
            .executes(context -> {
                ModUtils.swapItems(context.getSource().getPlayerOrException(), EquipmentSlot.HEAD, EquipmentSlot.MAINHAND);
                return 1;
            }));
    }
}
