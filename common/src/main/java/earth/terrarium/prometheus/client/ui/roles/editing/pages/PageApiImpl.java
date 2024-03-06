package earth.terrarium.prometheus.client.ui.roles.editing.pages;

import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.api.roles.client.PageApi;
import earth.terrarium.prometheus.client.ui.roles.editing.pages.permissions.PermissionPage;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import earth.terrarium.prometheus.common.roles.TeleportOptions;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PageApiImpl implements PageApi {

    private final Map<ResourceLocation, BiFunction<RoleEditContent, Runnable, Page>> factories = Util.make(new LinkedHashMap<>(), map -> {
        map.put(new ResourceLocation(Prometheus.MOD_ID, "permissions"), PermissionPage::new);
        map.put(CosmeticOptions.SERIALIZER.id(), CosmeticsPage::new);
        map.put(HomeOptions.SERIALIZER.id(), HomesPage::new);
        map.put(TeleportOptions.SERIALIZER.id(), TeleportOptionsPage::new);
    });

    @Override
    public void register(ResourceLocation id, BiFunction<RoleEditContent, Runnable, Page> factory) {
        if (factories.containsKey(id)) {
            throw new IllegalArgumentException("Page already registered for " + id);
        }
        factories.put(id, factory);
    }

    @Override
    public Map<ResourceLocation, BiFunction<RoleEditContent, Runnable, Page>> values() {
        return factories;
    }
}
