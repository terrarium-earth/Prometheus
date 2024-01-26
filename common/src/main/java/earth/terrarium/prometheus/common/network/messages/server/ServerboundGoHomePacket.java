package earth.terrarium.prometheus.common.network.messages.server;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import earth.terrarium.prometheus.common.handlers.locations.HomeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record ServerboundGoHomePacket() implements Packet<ServerboundGoHomePacket> {

    public static final ServerboundPacketType<ServerboundGoHomePacket> TYPE = new Type();

    @Override
    public PacketType<ServerboundGoHomePacket> type() {
        return TYPE;
    }

    private static class Type implements ServerboundPacketType<ServerboundGoHomePacket> {

        @Override
        public Class<ServerboundGoHomePacket> type() {
            return ServerboundGoHomePacket.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Prometheus.MOD_ID, "go_home");
        }

        @Override
        public void encode(ServerboundGoHomePacket message, FriendlyByteBuf buffer) {}

        @Override
        public ServerboundGoHomePacket decode(FriendlyByteBuf buffer) {
            return new ServerboundGoHomePacket();
        }

        @Override
        public Consumer<Player> handle(ServerboundGoHomePacket message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (!HomeHandler.teleport(serverPlayer)) {
                        var homes = LocationsApi.API.getHomes(serverPlayer);
                        if (homes.size() > 1) {
                            ServerboundOpenLocationPacket.openHomes(serverPlayer);
                        }
                    }
                }
            };
        }
    }
}
