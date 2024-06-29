package earth.terrarium.prometheus.client.handlers;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import earth.terrarium.prometheus.Prometheus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DimensionIconsListener extends SimplePreparableReloadListener<Map<ResourceKey<Level>, ResourceLocation>> {

    public static final DimensionIconsListener INSTANCE = new DimensionIconsListener();

    private static final ResourceLocation LOCATION = Prometheus.id("dimensions.json");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<Map<ResourceKey<Level>, ResourceLocation>> CODEC = Codec.unboundedMap(
        Level.RESOURCE_KEY_CODEC,
        ResourceLocation.CODEC
    );

    private static final Map<ResourceKey<Level>, ResourceLocation> icons = new HashMap<>();

    @Override
    protected @NotNull Map<ResourceKey<Level>, ResourceLocation> prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<Map<ResourceKey<Level>, ResourceLocation>> maps = manager.getResourceStack(LOCATION)
            .stream()
            .map(resource -> {
                try (var reader = resource.openAsReader()) {
                    return CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(reader))
                        .ifError(e -> LOGGER.error("Failed to load dimension icons: {}", e.message()))
                        .result()
                        .orElse(Map.of());
                } catch (Exception e) {
                    LOGGER.error("Failed to load dimension icons", e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();

        Map<ResourceKey<Level>, ResourceLocation> result = new HashMap<>();
        maps.forEach(result::putAll);
        return result;
    }

    @Override
    protected void apply(Map<ResourceKey<Level>, ResourceLocation> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        icons.clear();
        icons.putAll(object);
    }

    public static ResourceLocation getIcon(ResourceKey<Level> dimension) {
        return icons.get(dimension);
    }
}
