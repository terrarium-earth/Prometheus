package earth.terrarium.prometheus.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.api.roles.client.OptionDisplayApi;
import earth.terrarium.prometheus.client.screens.InvseeScreen;
import earth.terrarium.prometheus.client.screens.location.LocationScreen;
import earth.terrarium.prometheus.client.screens.roles.RolesScreen;
import earth.terrarium.prometheus.client.screens.roles.adding.MemberRolesScreen;
import earth.terrarium.prometheus.client.screens.roles.editing.RoleEditScreen;
import earth.terrarium.prometheus.client.screens.roles.options.displays.CosmeticOptionsDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.displays.HomeOptionsDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.displays.PermissionDisplay;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.CosmeticOptions;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.HomeOptions;
import earth.terrarium.prometheus.common.registries.ModMenus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.apache.commons.lang3.NotImplementedException;

public class PrometheusClient {

    public static void init() {
        register(ModMenus.INVSEE.get(), InvseeScreen::new);
        register(ModMenus.LOCATION.get(), LocationScreen::new);
        register(ModMenus.ROLES.get(), RolesScreen::new);
        register(ModMenus.ROLE_EDIT.get(), RoleEditScreen::new);
        register(ModMenus.MEMBER_ROLES.get(), MemberRolesScreen::new);

        OptionDisplayApi.API.register(new ResourceLocation(Prometheus.MOD_ID, "permissions"), PermissionDisplay::create);
        OptionDisplayApi.API.register(CosmeticOptions.SERIALIZER.id(), CosmeticOptionsDisplay::create);
        OptionDisplayApi.API.register(HomeOptions.SERIALIZER.id(), HomeOptionsDisplay::create);

        addAutoCompletes();
    }

    private static void addAutoCompletes() {
        var api = PermissionApi.API;
        api.addAutoComplete("headings.streaming");
        api.addAutoComplete("headings.recording");
        api.addAutoComplete("headings.afk");
        api.addAutoComplete("headings.dnd");
        api.addAutoComplete("headings.music");
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
