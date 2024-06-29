package earth.terrarium.prometheus.client.screens.location;

import earth.terrarium.prometheus.api.locations.client.LocationDisplayApi;
import earth.terrarium.prometheus.client.handlers.DimensionIconsListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class LocationDisplayApiImpl implements LocationDisplayApi {

    @Override
    public @Nullable ResourceLocation getIcon(ResourceKey<Level> dimension) {
        return DimensionIconsListener.getIcon(dimension);
    }
}
