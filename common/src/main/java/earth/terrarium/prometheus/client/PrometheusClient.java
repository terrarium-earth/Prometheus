package earth.terrarium.prometheus.client;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.client.screens.location.LocationDisplayApiImpl;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissionHandler;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.ServerboundOpenLocationPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PrometheusClient {

    public static final KeyMapping OPEN_HOMES = new KeyMapping(
        "key.prometheus.open_homes",
        InputConstants.UNKNOWN.getValue(),
        "key.categories.odyssey"
    );

    public static final List<KeyMapping> KEYS = List.of(OPEN_HOMES);

    public static void init() {
        addAutoCompletes();
        addIcons();
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
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("twilightforest:twilight_forest")),
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
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("rats:ratlantis")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_rats_ratlantis.png"));
        api.register(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("aether:the_aether")),
            new ResourceLocation(Prometheus.MOD_ID, "textures/gui/locations/icon_aether_the_aether.png"));
    }

    public static void clientTick() {
        if (OPEN_HOMES.consumeClick()) {
            NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenLocationPacket(LocationType.HOME));
        }
    }
}
