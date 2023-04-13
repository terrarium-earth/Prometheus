package earth.terrarium.prometheus.client.screens.location;

import earth.terrarium.prometheus.api.locations.client.LocationDisplayApi;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LocationDisplayApiImpl implements LocationDisplayApi {

    private static final Map<ResourceKey<Level>, ResourceLocation> ICONS = new HashMap<>();

    @Override
    public void register(ResourceKey<Level> dimension, ResourceLocation icon) {
        ICONS.put(dimension, icon);
    }

    @Override
    public @Nullable ResourceLocation getIcon(ResourceKey<Level> dimension) {
        return ICONS.get(dimension);
    }
}
