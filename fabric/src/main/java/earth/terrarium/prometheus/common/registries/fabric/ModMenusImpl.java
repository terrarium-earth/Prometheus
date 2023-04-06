package earth.terrarium.prometheus.common.registries.fabric;

import earth.terrarium.prometheus.common.registries.ModMenus;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenusImpl {
    public static <T extends AbstractContainerMenu> MenuType<T> createMenu(ModMenus.MenuSupplier<T> supplier) {
        return new ExtendedScreenHandlerType<>(supplier::create);
    }
}
