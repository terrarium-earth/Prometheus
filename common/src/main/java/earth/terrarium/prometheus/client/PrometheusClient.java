package earth.terrarium.prometheus.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.prometheus.client.screens.InvseeScreen;
import earth.terrarium.prometheus.client.screens.LocationScreen;
import earth.terrarium.prometheus.common.registries.ModMenus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.apache.commons.lang3.NotImplementedException;

public class PrometheusClient {

    public static void init() {
        register(ModMenus.INVSEE.get(), InvseeScreen::new);
        register(ModMenus.LOCATION.get(), LocationScreen::new);
    }

    @ExpectPlatform
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, ScreenConstructor<M, U> factory) {
        throw new NotImplementedException();
    }

    @Environment(EnvType.CLIENT)
    public interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T menu, Inventory inventory, Component component);
    }
}
