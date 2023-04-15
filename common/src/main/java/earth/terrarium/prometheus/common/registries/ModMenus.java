package earth.terrarium.prometheus.common.registries;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.menus.*;
import earth.terrarium.prometheus.common.menus.location.LocationMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

    public static final ResourcefulRegistry<MenuType<?>> MENUS = ResourcefulRegistries.create(BuiltInRegistries.MENU, Prometheus.MOD_ID);

    public static final RegistryEntry<MenuType<InvseeMenu>> INVSEE = MENUS.register("invsee", () -> createMenu(InvseeMenu::new));
    public static final RegistryEntry<MenuType<LocationMenu>> LOCATION = MENUS.register("location", () -> createMenu(LocationMenu::new));
    public static final RegistryEntry<MenuType<RolesMenu>> ROLES = MENUS.register("roles", () -> createMenu(RolesMenu::new));
    public static final RegistryEntry<MenuType<RoleEditMenu>> ROLE_EDIT = MENUS.register("role_edit", () -> createMenu(RoleEditMenu::new));
    public static final RegistryEntry<MenuType<MemberRolesMenu>> MEMBER_ROLES = MENUS.register("member_roles", () -> createMenu(MemberRolesMenu::new));
    public static final RegistryEntry<MenuType<EditCommandMenu>> EDIT_COMMAND = MENUS.register("edit_command", () -> createMenu(EditCommandMenu::new));

    @ExpectPlatform
    private static <T extends AbstractContainerMenu> MenuType<T> createMenu(MenuSupplier<T> supplier) {
        throw new NotImplementedException();
    }

    public interface MenuSupplier<T extends AbstractContainerMenu> {
        T create(int i, Inventory inventory, FriendlyByteBuf buf);
    }
}
