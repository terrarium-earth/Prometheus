package earth.terrarium.prometheus.client.utils;

import earth.terrarium.prometheus.common.handlers.heading.Heading;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.UUID;

public interface ClientListenerHook {

    Map<UUID, Heading> prometheus$getHeadings();

    Map<UUID, Component> prometheus$getHeadingTexts();

    void prometheus$setHeading(UUID uuid, Heading heading);

    void prometheus$setHeadingText(UUID uuid, Component text);
}
