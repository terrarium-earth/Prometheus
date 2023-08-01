package earth.terrarium.prometheus.api.locations;

import com.mojang.datafixers.util.Either;
import earth.terrarium.prometheus.api.ApiHelper;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public interface LocationsApi {

    LocationsApi API = ApiHelper.load(LocationsApi.class);

    /**
     * Gets the homes of a player
     *
     * @param player the player to get the homes of
     * @return the homes of the player
     */
    Map<String, GlobalPos> getHomes(ServerPlayer player);

    /**
     * Gets a home of a player
     * @param player the player to get the home of
     * @param name the name of the home
     * @return the home of the player or an error
     */
    Either<Location, LocationError> getHome(ServerPlayer player, String name);

    /**
     * Gets the warps of a server
     *
     * @param server the server to get the warps of
     * @return the warps of the server
     */
    Map<String, GlobalPos> getWarps(MinecraftServer server);

    /**
     * Gets a warp of a server
     * @param server the server to get the warp of
     * @param name the name of the warp
     * @return the warp of the server or an error
     */
    Either<Location, LocationError> getWarp(MinecraftServer server, String name);
}