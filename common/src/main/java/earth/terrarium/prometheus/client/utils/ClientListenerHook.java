package earth.terrarium.prometheus.client.utils;

import earth.terrarium.prometheus.common.handlers.heading.Heading;

import java.util.Map;
import java.util.UUID;

public interface ClientListenerHook {

    Map<UUID, Heading> prometheus$getHeadings();

    void prometheus$setHeading(UUID uuid, Heading heading);
}
