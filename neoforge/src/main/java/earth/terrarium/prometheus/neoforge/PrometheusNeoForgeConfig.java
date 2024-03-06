package earth.terrarium.prometheus.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.permission.handler.DefaultPermissionHandler;

public class PrometheusNeoForgeConfig {

    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> PARENT_PERMISSION_HANDLER = SERVER_BUILDER
        .comment("The permission handler to use as a parent for the Prometheus permission handler.")
        .define("parentPermissionHandler", DefaultPermissionHandler.IDENTIFIER.toString());

    public static final ModConfigSpec SERVER_CONFIG = SERVER_BUILDER.build();
}
