package earth.terrarium.prometheus.common.handlers;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.world.entity.player.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class CooldownHandler {

    private static final Map<UUID, Object2LongMap<String>> COOLDOWNS = new HashMap<>();

    private static Object2LongMap<String> getCooldowns(Player player) {
        return COOLDOWNS.computeIfAbsent(player.getUUID(), uuid -> new Object2LongOpenHashMap<>());
    }

    public static void setCooldown(Player player, String id, Duration duration) {
        getCooldowns(player).put(id, System.currentTimeMillis() + duration.toMillis());
    }

    public static void setCooldown(Player player, String id, TimeUnit unit, long duration) {
        setCooldown(player, id, Duration.of(duration, unit.toChronoUnit()));
    }

    public static void setCooldown(Player player, String id, long duration) {
        setCooldown(player, id, TimeUnit.MILLISECONDS, duration);
    }

    public static boolean hasCooldown(Player player, String id) {
        var cooldowns = getCooldowns(player);
        if (!cooldowns.containsKey(id)) return false;
        if (cooldowns.getLong(id) < System.currentTimeMillis()) {
            cooldowns.removeLong(id);
            return false;
        }
        return true;
    }

    public static long getCooldown(Player player, String id) {
        return getCooldowns(player).getLong(id);
    }

}
