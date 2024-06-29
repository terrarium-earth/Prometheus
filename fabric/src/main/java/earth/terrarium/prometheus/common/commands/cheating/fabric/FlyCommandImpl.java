package earth.terrarium.prometheus.common.commands.cheating.fabric;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class FlyCommandImpl {
    public static void setCanFly(Entity entity) {
        if (entity instanceof Player player) {
            player.getAbilities().flying = !player.getAbilities().flying;
            player.getAbilities().mayfly = !player.getAbilities().mayfly;
            player.onUpdateAbilities();
        }
    }
}
