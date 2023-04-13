package earth.terrarium.prometheus.api.locations.client;

import earth.terrarium.prometheus.api.ApiHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface LocationDisplayApi {

    LocationDisplayApi API = ApiHelper.load(LocationDisplayApi.class);

    /**
     * Register a location icon for a dimension.
     *
     * @param dimension The dimension to register the icon for.
     * @param icon      The icon to register, must be 16x16 pixels.
     */
    void register(ResourceKey<Level> dimension, ResourceLocation icon);

    /**
     * Get the icon for a dimension.
     *
     * @param dimension The dimension to get the icon for.
     * @return The icon for the dimension, or a default icon if none is registered.
     */
    @Nullable
    ResourceLocation getIcon(ResourceKey<Level> dimension);
}
