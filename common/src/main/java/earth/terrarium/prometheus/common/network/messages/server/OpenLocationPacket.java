package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.common.handlers.locations.WarpHandler;
import earth.terrarium.prometheus.common.menus.content.location.Location;
import earth.terrarium.prometheus.common.menus.content.location.LocationContent;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.OpenLocationScreenPacket;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record OpenLocationPacket(LocationType type) implements Packet<OpenLocationPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_location");
    public static final PacketHandler<OpenLocationPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenLocationPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenLocationPacket> {

        @Override
        public void encode(OpenLocationPacket message, FriendlyByteBuf buffer) {
            buffer.writeEnum(message.type());
        }

        @Override
        public OpenLocationPacket decode(FriendlyByteBuf buffer) {
            return new OpenLocationPacket(buffer.readEnum(LocationType.class));
        }

        @Override
        public PacketContext handle(OpenLocationPacket message) {
            return (player, level) -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    switch (message.type()) {
                        case WARP -> openWarps(serverPlayer);
                        case HOME -> openHomes(serverPlayer);
                    }
                }
            };
        }
    }

    public static void openWarps(ServerPlayer player) {
        Map<String, GlobalPos> homes = LocationsApi.API.getWarps(player.server);
        List<Location> locations = homes.entrySet()
            .stream()
            .map(entry -> new Location(entry.getKey(), entry.getValue()))
            .toList();

        int maxAmount = WarpHandler.canModifyWarps(player) ? Integer.MAX_VALUE : -1;
        NetworkHandler.CHANNEL.sendToPlayer(new OpenLocationScreenPacket(new LocationContent(
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
        NetworkHandler.CHANNEL.sendToPlayer(new OpenLocationScreenPacket(new LocationContent(
            LocationType.HOME,
            maxHomes,
            locations
        )), player);
    }
}
