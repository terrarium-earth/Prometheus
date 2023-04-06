package earth.terrarium.prometheus.common.registries;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.menus.InvseeMenu;
import earth.terrarium.prometheus.common.menus.LocationMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

    public static final ResourcefulRegistry<MenuType<?>> MENUS = ResourcefulRegistries.create(BuiltInRegistries.MENU, Prometheus.MOD_ID);

    public static final RegistryEntry<MenuType<InvseeMenu>> INVSEE = MENUS.register("invsee", () -> createMenu(InvseeMenu::new));
    public static final RegistryEntry<MenuType<LocationMenu>> LOCATION = MENUS.register("location", () -> createMenu(LocationMenu::new));

    @ExpectPlatform
    private static <T extends AbstractContainerMenu> MenuType<T> createMenu(MenuSupplier<T> supplier) {
        throw new NotImplementedException();
    }

    public interface MenuSupplier<T extends AbstractContainerMenu> {
        T create(int i, Inventory inventory, FriendlyByteBuf buf);
    }
}
