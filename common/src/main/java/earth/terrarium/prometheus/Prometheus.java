package earth.terrarium.prometheus;

import earth.terrarium.prometheus.common.handlers.role.options.OptionRegistry;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.DisplayOptions;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.HomeOptions;
import earth.terrarium.prometheus.common.network.NetworkHandler;

public class Prometheus {
    public static final String MOD_ID = "prometheus";

    public static void init() {
        NetworkHandler.init();
        OptionRegistry.INSTANCE.register(DisplayOptions.SERIALIZER);
        OptionRegistry.INSTANCE.register(HomeOptions.SERIALIZER);
    }

    public static void postInit() {
        OptionRegistry.freeze();
    }
}