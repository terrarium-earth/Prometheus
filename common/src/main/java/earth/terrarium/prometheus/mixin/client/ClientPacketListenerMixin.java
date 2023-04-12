package earth.terrarium.prometheus.mixin.client;

import earth.terrarium.prometheus.client.utils.ClientListenerHook;
import earth.terrarium.prometheus.common.handlers.heading.Heading;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements ClientListenerHook {

    private final Map<UUID, Heading> prometheus$headings = new HashMap<>();

    @Override
    public Map<UUID, Heading> prometheus$getHeadings() {
        return prometheus$headings;
    }

    @Override
    public void prometheus$setHeading(UUID uuid, Heading heading) {
        prometheus$headings.put(uuid, heading);
    }
}
