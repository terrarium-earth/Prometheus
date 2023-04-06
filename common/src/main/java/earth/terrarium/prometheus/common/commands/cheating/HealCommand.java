package earth.terrarium.prometheus.common.commands.cheating;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class HealCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("heal")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("entities", EntityArgument.entities())
                    .executes(context -> {
                        EntityArgument.getEntities(context, "entities").forEach(HealCommand::heal);
                        return 1;
                    })
                ).executes(context -> {
                    HealCommand.heal(context.getSource().getEntity());
                    return 1;
                }));
    }

    private static void heal(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(livingEntity.getMaxHealth());
            livingEntity.clearFire();
            livingEntity.setTicksFrozen(0);
            livingEntity.setAirSupply(livingEntity.getMaxAirSupply());
            final var effects = List.copyOf(livingEntity.getActiveEffects());
            for (MobEffectInstance effect : effects) {
                if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                    livingEntity.removeEffect(effect.getEffect());
                }
            }
        }
    }
}
