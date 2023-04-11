package earth.terrarium.prometheus.common.handlers.role;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.CosmeticOptions;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.HomeOptions;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultRole {

    public static final UUID DEFAULT_ROLE = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private static final Map<String, TriState> PERMISSIONS = Util.make(new HashMap<>(), map -> {
        map.put("headings.afk", TriState.TRUE);
        map.put("headings.dnd", TriState.TRUE);
    });

    private static final Map<ResourceLocation, RoleOption<?>> OPTIONS = Util.make(new HashMap<>(), map -> {
        map.put(CosmeticOptions.SERIALIZER.id(), new CosmeticOptions("@Everyone", "⛏", Color.DEFAULT));
        map.put(HomeOptions.SERIALIZER.id(), new HomeOptions(5));
    });

    public static Role create() {
        return new Role(PERMISSIONS, OPTIONS);
    }
}
