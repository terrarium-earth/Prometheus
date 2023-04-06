package earth.terrarium.prometheus.common.registries.forge;

import earth.terrarium.prometheus.common.registries.ModMenus;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class ModMenusImpl {
    public static <T extends AbstractContainerMenu> MenuType<T> createMenu(ModMenus.MenuSupplier<T> supplier) {
        return IForgeMenuType.create(supplier::create);
    }
}
