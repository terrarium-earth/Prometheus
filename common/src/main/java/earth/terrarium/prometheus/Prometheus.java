package earth.terrarium.prometheus;

import earth.terrarium.prometheus.common.network.NetworkHandler;

public class Prometheus {
    public static final String MOD_ID = "prometheus";

    public static void init() {
        NetworkHandler.init();
    }
}