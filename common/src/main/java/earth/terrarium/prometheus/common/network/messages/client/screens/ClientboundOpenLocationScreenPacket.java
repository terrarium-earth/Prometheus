package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.client.screens.location.LocationScreen;
import earth.terrarium.prometheus.common.handlers.locations.WarpHandler;
import earth.terrarium.prometheus.common.menus.content.location.Location;
import earth.terrarium.prometheus.common.menus.content.location.LocationContent;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ClientboundOpenLocationScreenPacket(
    LocationContent content
) implements Packet<ClientboundOpenLocationScreenPacket> {

    public static final ClientboundPacketType<ClientboundOpenLocationScreenPacket> TYPE = CodecPacketType.Client.create(
            Prometheus.id("open_location_screen"),
            LocationContent.BYTE_CODEC.map(ClientboundOpenLocationScreenPacket::new, ClientboundOpenLocationScreenPacket::content),
            NetworkHandle.handle(message -> LocationScreen.open(message.content()))
    );

    @Override
    public PacketType<ClientboundOpenLocationScreenPacket> type() {
        return TYPE;
    }

    public static void openWarps(ServerPlayer player) {
        Map<String, GlobalPos> homes = LocationsApi.API.getWarps(player.server);
        List<Location> locations = homes.entrySet()
            .stream()
            .map(entry -> new Location(entry.getKey(), entry.getValue()))
            .toList();

        int maxAmount = WarpHandler.canModifyWarps(player) ? Integer.MAX_VALUE : -1;
        NetworkHandler.CHANNEL.sendToPlayer(new ClientboundOpenLocationScreenPacket(new LocationContent(
            LocationType.WARP,
            maxAmount,
            locations
        )), player);
    }

    public static void openHomes(ServerPlayer player) {
        Map<String, GlobalPos> homes = LocationsApi.API.getHomes(player);
        List<Location> locations = homes.entrySet()
            .stream()
            .map(entry -> new Location(entry.getKey(), entry.getValue()))
            .toList();

        int maxHomes = Objects.requireNonNull(RoleApi.API.getOption(player, HomeOptions.SERIALIZER)).max();
        NetworkHandler.CHANNEL.sendToPlayer(new ClientboundOpenLocationScreenPacket(new LocationContent(
            LocationType.HOME,
            maxHomes,
            locations
        )), player);
    }
}
