package earth.terrarium.prometheus.common.handlers.heading;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface HeadingEntityHook {

    default void prometheus$setHeadingAndUpdate(Heading heading) {
        prometheus$setHeading(heading);
        prometheus$setHeadingText(Heading.getInitalComponent(heading));
    }

    void prometheus$setHeading(Heading heading);

    Heading prometheus$getHeading();

    Component prometheus$getHeadingText();

    void prometheus$setHeadingText(@Nullable Component text);
}
