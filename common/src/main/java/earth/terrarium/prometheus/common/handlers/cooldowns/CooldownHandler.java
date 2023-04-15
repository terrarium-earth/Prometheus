package earth.terrarium.prometheus.common.handlers.cooldowns;

import net.minecraft.world.entity.player.Player;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class CooldownHandler {

    public static void setCooldown(Player player, String id, Duration duration) {
        if (player instanceof CooldownHook hook) {
            hook.prometheus$setCooldown(id, duration);
        }
    }

    public static void setCooldown(Player player, String id, TimeUnit unit, long duration) {
        setCooldown(player, id, Duration.of(duration, unit.toChronoUnit()));
    }

    public static void setCooldown(Player player, String id, long duration) {
        setCooldown(player, id, TimeUnit.MILLISECONDS, duration);
    }

    public static boolean hasCooldown(Player player, String id) {
        if (player instanceof CooldownHook hook) {
            return hook.prometheus$hasCooldown(id);
        }
        return false;
    }

    private CooldownHandler() {
    }

}
