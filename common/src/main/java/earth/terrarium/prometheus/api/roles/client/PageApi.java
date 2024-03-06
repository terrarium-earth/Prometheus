package earth.terrarium.prometheus.api.roles.client;

import earth.terrarium.prometheus.api.ApiHelper;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.BiFunction;

public interface PageApi {

    PageApi API = ApiHelper.load(PageApi.class);

    void register(ResourceLocation id, BiFunction<RoleEditContent, Runnable, Page> factory);

    Map<ResourceLocation, BiFunction<RoleEditContent, Runnable, Page>> values();
}
