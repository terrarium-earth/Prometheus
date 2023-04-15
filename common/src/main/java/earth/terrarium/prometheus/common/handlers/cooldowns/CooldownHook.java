package earth.terrarium.prometheus.common.handlers.cooldowns;

import java.time.Duration;

public interface CooldownHook {

    void prometheus$setCooldown(String id, Duration duration);

    boolean prometheus$hasCooldown(String id);
}
