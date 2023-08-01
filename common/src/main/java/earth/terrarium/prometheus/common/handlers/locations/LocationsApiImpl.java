package earth.terrarium.prometheus.common.handlers.locations;

import com.mojang.datafixers.util.Either;
import earth.terrarium.prometheus.api.locations.Location;
import earth.terrarium.prometheus.api.locations.LocationError;
import earth.terrarium.prometheus.api.locations.LocationsApi;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class LocationsApiImpl implements LocationsApi {

    @Override
    public Map<String, GlobalPos> getHomes(ServerPlayer player) {
        return HomeHandler.getHomes(player);
    }

    @Override
    public Either<Location, LocationError> getHome(ServerPlayer player, String name) {
        Map<String, GlobalPos> homes = HomeHandler.getHomes(player);
        if (homes.isEmpty()) return Either.right(LocationError.NO_LOCATIONS);
        if (!homes.containsKey(name)) return Either.right(LocationError.DOES_NOT_EXIST_WITH_NAME);
        GlobalPos pos = homes.get(name);
        if (pos == null) return Either.right(LocationError.NO_DIMENSION_FOR_LOCATION);
        ServerLevel level = player.server.getLevel(pos.dimension());
        if (level == null) return Either.right(LocationError.NO_DIMENSION_FOR_LOCATION);
        return Either.left(new Location(level, pos.pos()));
    }

    @Override
    public Map<String, GlobalPos> getWarps(MinecraftServer server) {
        return WarpHandler.getWarps(server);
    }

    @Override
    public Either<Location, LocationError> getWarp(MinecraftServer server, String name) {
        Map<String, GlobalPos> warps = WarpHandler.getWarps(server);
        if (warps.isEmpty()) return Either.right(LocationError.NO_LOCATIONS);
        if (!warps.containsKey(name)) return Either.right(LocationError.DOES_NOT_EXIST_WITH_NAME);
        GlobalPos pos = warps.get(name);
        if (pos == null) return Either.right(LocationError.NO_DIMENSION_FOR_LOCATION);
        ServerLevel level = server.getLevel(pos.dimension());
        if (level == null) return Either.right(LocationError.NO_DIMENSION_FOR_LOCATION);
        return Either.left(new Location(level, pos.pos()));
    }
}
