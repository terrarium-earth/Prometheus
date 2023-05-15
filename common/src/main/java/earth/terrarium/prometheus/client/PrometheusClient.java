package earth.terrarium.prometheus.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.api.roles.client.OptionDisplayApi;
import earth.terrarium.prometheus.client.screens.InvseeScreen;
import earth.terrarium.prometheus.client.screens.commands.EditCommandScreen;
import earth.terrarium.prometheus.client.screens.location.LocationDisplayApiImpl;
import earth.terrarium.prometheus.client.screens.location.LocationScreen;
import earth.terrarium.prometheus.client.screens.roles.RolesScreen;
import earth.terrarium.prometheus.client.screens.roles.adding.MemberRolesScreen;
import earth.terrarium.prometheus.client.screens.roles.editing.RoleEditScreen;
import earth.terrarium.prometheus.client.screens.roles.options.displays.CosmeticOptionsDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.displays.HomeOptionsDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.displays.PermissionDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.displays.TeleportOptionsDisplay;
import earth.terrarium.prometheus.client.utils.SystemNotificationUtils;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissionHandler;
import earth.terrarium.prometheus.common.registries.ModMenus;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import earth.terrarium.prometheus.common.roles.TeleportOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
        register(ModMenus.EDIT_COMMAND.get(), EditCommandScreen::new);

        OptionDisplayApi.API.register(new ResourceLocation(Prometheus.MOD_ID, "permissions"), PermissionDisplay::create);
        OptionDisplayApi.API.register(CosmeticOptions.SERIALIZER.id(), CosmeticOptionsDisplay::create);
        OptionDisplayApi.API.register(HomeOptions.SERIALIZER.id(), HomeOptionsDisplay::create);
        OptionDisplayApi.API.register(TeleportOptions.SERIALIZER.id(), TeleportOptionsDisplay::create);

        addAutoCompletes();
        addIcons();

        SystemNotificationUtils.init();
    }

    private static void addAutoCompletes() {
        var api = PermissionApi.API;
        api.addAutoComplete("headings.streaming");
        api.addAutoComplete("headings.recording");
        api.addAutoComplete("headings.afk");
        api.addAutoComplete("headings.dnd");
        api.addAutoComplete("headings.music");
        api.addAutoComplete("roles.manage");
        api.addAutoComplete("warps.manage");
        api.addAutoComplete("warps.manage");

        api.addAutoComplete(() -> CommandPermissionHandler.COMMAND_PERMS);
    }

    private static void addIcons() {
        var api = LocationDisplayApiImpl.API;
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("overworld")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_vanilla_overworld.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_nether")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_vanilla_nether.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_end")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_vanilla_the_end.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("the_bumblezone:the_bumblezone")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_bumblezone_bumblezone.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("twilightforest:twilightforest")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_twilightforest_twilight_forest.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("undergarden:undergarden")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_undergarden_the_undergarden.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("edenring:edenring")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_edenring_eden_ring.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("deeperdarker:otherside")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_deeperdarker_the_otherside.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("blue_skies:everdawn")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_blue_skies_everdawn.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("blue_skies:everbright")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_blue_skies_everbright.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("mining_dimension:mining")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_advancedminingdimension_advanced_mining_dimension.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:earth_orbit")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_orbit.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:galcio_orbit")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_orbit.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:mars_orbit")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_orbit.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:mercury_orbit")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_orbit.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:moon_orbit")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_orbit.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:venus_orbit")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_orbit.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:venus")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_venus.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:moon")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_moon.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:mercury")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_mercury.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:mars")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_mars.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ad_astra:galcio")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_adastra_glacio.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:frost_world")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_abyssii_frost_world.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:pocket_dimension")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_abyssii_pocket_dimension.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("theabyss:the_abyss")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_abyssii_the_abyss.png"));
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
