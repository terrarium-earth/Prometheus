package earth.terrarium.prometheus.api.events;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This event is fired when roles are updated globally.
 * This is fired when a role has its permissions changed or options changed or a role has been moved.
 * @param server The server that the roles were updated on.
 */
public record ServerRolesUpdatedEvent(MinecraftServer server) {

    private static final List<Consumer<ServerRolesUpdatedEvent>> LISTENERS = new ArrayList<>();

    public static void register(Consumer<ServerRolesUpdatedEvent> listener) {
        LISTENERS.add(listener);
    }

    public static void unregister(Consumer<ServerRolesUpdatedEvent> listener) {
        LISTENERS.remove(listener);
    }

    @ApiStatus.Internal
    public static void fire(ServerRolesUpdatedEvent event) {
        LISTENERS.forEach(listener -> listener.accept(event));
    }
}
