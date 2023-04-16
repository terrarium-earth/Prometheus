package earth.terrarium.prometheus;

import earth.terrarium.prometheus.common.handlers.role.OptionRegistry;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.registries.ModMenus;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import earth.terrarium.prometheus.common.roles.TeleportOptions;

public class Prometheus {
    public static final String MOD_ID = "prometheus";

    public static void init() {
        NetworkHandler.init();
        ModMenus.MENUS.init();
        OptionRegistry.INSTANCE.register(CosmeticOptions.SERIALIZER);
        OptionRegistry.INSTANCE.register(HomeOptions.SERIALIZER);
        OptionRegistry.INSTANCE.register(TeleportOptions.SERIALIZER);
    }

    public static void postInit() {
        OptionRegistry.freeze();
    }
}