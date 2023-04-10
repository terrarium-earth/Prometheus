package earth.terrarium.prometheus.client.screens.roles.options;

import earth.terrarium.prometheus.api.roles.client.OptionDisplayApi;
import earth.terrarium.prometheus.api.roles.client.OptionDisplayFactory;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class OptionDisplayApiImpl implements OptionDisplayApi {

    private final Map<ResourceLocation, OptionDisplayFactory> factories = new HashMap<>();

    @Override
    public void register(ResourceLocation id, OptionDisplayFactory factory) {
        if (factories.containsKey(id)) {
            throw new IllegalArgumentException("OptionDisplayFactory already registered for " + id);
        }
        factories.put(id, factory);
    }

    @Override
    public OptionDisplayFactory get(ResourceLocation id) {
        return factories.get(id);
    }

    @Override
    public Map<ResourceLocation, OptionDisplayFactory> values() {
        return Map.copyOf(factories);
    }

}
