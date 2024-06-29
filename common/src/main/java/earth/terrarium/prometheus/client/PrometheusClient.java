package earth.terrarium.prometheus.client;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.client.handlers.DimensionIconsListener;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissions;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.ServerboundOpenLocationPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.List;
import java.util.function.BiConsumer;

public class PrometheusClient {

    public static final KeyMapping OPEN_HOMES = new KeyMapping(
        "key.prometheus.open_homes",
        InputConstants.UNKNOWN.getValue(),
        "key.categories.project_odyssey"
    );

    public static final List<KeyMapping> KEYS = List.of(OPEN_HOMES);

    public static void init() {
        addAutoCompletes();
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

        api.addAutoComplete(CommandPermissions::getCommandPermissions);
    }

    public static void clientTick() {
        if (OPEN_HOMES.consumeClick()) {
            NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenLocationPacket(LocationType.HOME));
        }
    }

    public static void initReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> init) {
        init.accept(Prometheus.id("dimensions"), DimensionIconsListener.INSTANCE);
    }
}
