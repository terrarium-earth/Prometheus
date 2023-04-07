package earth.terrarium.prometheus.common.handlers.role;

import earth.terrarium.prometheus.api.TriState;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;

public class DefaultRole {

    private static final Map<String, TriState> PERMISSIONS = Util.make(new HashMap<>(), map -> {
        map.put("prometheus.heading.afk", TriState.TRUE);
        map.put("prometheus.heading.dnd", TriState.TRUE);
    });

    public static Role create() {
        return new Role(PERMISSIONS, new HashMap<>());
    }
}
