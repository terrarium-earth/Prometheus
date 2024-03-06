package earth.terrarium.prometheus.fabric;

import earth.terrarium.prometheus.api.permissions.PermissionApi;
import me.lucko.fabric.api.permissions.v0.OfflinePermissionCheckEvent;
import me.lucko.fabric.api.permissions.v0.PermissionCheckEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class PrometheusFabricPermissionHandler {

    private static MinecraftServer server = null;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> PrometheusFabricPermissionHandler.server = server);

        PermissionCheckEvent.EVENT.register((source, permission) -> {
            if (source instanceof CommandSourceStack stack && stack.isPlayer()) {
                ServerPlayer player = stack.getPlayer();
                return switch (PermissionApi.API.getPermission(player, permission)) {
                    case UNDEFINED -> TriState.DEFAULT;
                    case TRUE -> TriState.TRUE;
                    case FALSE -> TriState.FALSE;
                };
            }
            return TriState.DEFAULT;
        });

        OfflinePermissionCheckEvent.EVENT.register((uuid, permission) -> {
            if (PrometheusFabricPermissionHandler.server != null) {
                TriState state = switch (PermissionApi.API.getOfflinePermission(PrometheusFabricPermissionHandler.server, uuid, permission)) {
                    case UNDEFINED -> TriState.DEFAULT;
                    case TRUE -> TriState.TRUE;
                    case FALSE -> TriState.FALSE;
                };
                return CompletableFuture.completedFuture(state);
            }
            return CompletableFuture.completedFuture(TriState.DEFAULT);
        });
    }
}
