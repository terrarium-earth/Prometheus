package earth.terrarium.prometheus.mixin.common;

import earth.terrarium.prometheus.common.handlers.cooldowns.CooldownHook;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

import java.time.Duration;

@Mixin(Player.class)
public abstract class CooldownPlayerMixin implements CooldownHook {

    private final Object2LongMap<String> prometheus$cooldowns = new Object2LongOpenHashMap<>();

    @Override
    public void prometheus$setCooldown(String id, Duration duration) {
        prometheus$cooldowns.put(id, System.currentTimeMillis() + duration.toMillis());
    }

    @Override
    public long prometheus$getCooldown(String id) {
        return prometheus$cooldowns.getLong(id);
    }

    @Override
    public boolean prometheus$hasCooldown(String id) {
        if (!prometheus$cooldowns.containsKey(id)) {
            return false;
        }
        if (prometheus$cooldowns.getLong(id) < System.currentTimeMillis()) {
            prometheus$cooldowns.removeLong(id);
            return false;
        }
        return true;
    }
}
