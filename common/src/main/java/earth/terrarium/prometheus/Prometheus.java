package earth.terrarium.prometheus;

import earth.terrarium.prometheus.common.handlers.role.options.OptionRegistry;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.CosmeticOptions;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.HomeOptions;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.registries.ModMenus;

public class Prometheus {
    public static final String MOD_ID = "prometheus";

    public static void init() {
        NetworkHandler.init();
        ModMenus.MENUS.init();
        OptionRegistry.INSTANCE.register(CosmeticOptions.SERIALIZER);
        OptionRegistry.INSTANCE.register(HomeOptions.SERIALIZER);
    }

    public static void postInit() {
        OptionRegistry.freeze();
    }
}