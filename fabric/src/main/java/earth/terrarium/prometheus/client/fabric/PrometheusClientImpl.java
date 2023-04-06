package earth.terrarium.prometheus.client.fabric;

import earth.terrarium.prometheus.client.PrometheusClient;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class PrometheusClientImpl {
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, PrometheusClient.ScreenConstructor<M, U> factory) {
        MenuScreens.register(type, factory::create);
    }
}
