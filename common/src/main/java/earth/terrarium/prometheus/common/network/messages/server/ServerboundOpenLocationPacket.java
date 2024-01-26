package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.common.handlers.locations.WarpHandler;
import earth.terrarium.prometheus.common.menus.content.location.Location;
import earth.terrarium.prometheus.common.menus.content.location.LocationContent;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenLocationScreenPacket;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public record ServerboundOpenLocationPacket(LocationType locationType) implements Packet<ServerboundOpenLocationPacket> {

    public static final ServerboundPacketType<ServerboundOpenLocationPacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundOpenLocationPacket> type() {
        return TYPE;
    }

    private static class Type extends CodecPacketType<ServerboundOpenLocationPacket> implements ServerboundPacketType<ServerboundOpenLocationPacket> {

        public Type() {
            super(
                ServerboundOpenLocationPacket.class,
                new ResourceLocation(Prometheus.MOD_ID, "open_location"),
                ObjectByteCodec.create(
                    ByteCodec.ofEnum(LocationType.class).fieldOf(ServerboundOpenLocationPacket::locationType),
                    ServerboundOpenLocationPacket::new
                )
            );
        }

        @Override
        public Consumer<Player> handle(ServerboundOpenLocationPacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    switch (message.locationType()) {
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
