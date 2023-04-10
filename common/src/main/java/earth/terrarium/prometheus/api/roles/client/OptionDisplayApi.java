package earth.terrarium.prometheus.api.roles.client;

import earth.terrarium.prometheus.api.ApiHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface OptionDisplayApi {

    OptionDisplayApi API = ApiHelper.load(OptionDisplayApi.class);

    void register(ResourceLocation id, OptionDisplayFactory factory);

    OptionDisplayFactory get(ResourceLocation id);

    Map<ResourceLocation, OptionDisplayFactory> values();
}
