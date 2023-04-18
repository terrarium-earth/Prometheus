package earth.terrarium.prometheus;

import earth.terrarium.prometheus.api.roles.options.RoleOptionsApi;
import earth.terrarium.prometheus.common.handlers.role.RoleOptionsApiImpl;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.registries.ModMenus;
import earth.terrarium.prometheus.common.registries.ModSounds;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import earth.terrarium.prometheus.common.roles.TeleportOptions;
import net.minecraft.SharedConstants;

public class Prometheus {
    public static final String MOD_ID = "prometheus";

    public static void init() {
        SharedConstants.IS_RUNNING_IN_IDE = true;
        NetworkHandler.init();
        ModMenus.MENUS.init();
        ModSounds.SOUNDS.init();
        RoleOptionsApi.API.register(CosmeticOptions.SERIALIZER);
        RoleOptionsApi.API.register(HomeOptions.SERIALIZER);
        RoleOptionsApi.API.register(TeleportOptions.SERIALIZER);
    }

    public static void postInit() {
        RoleOptionsApiImpl.freeze();
    }
}