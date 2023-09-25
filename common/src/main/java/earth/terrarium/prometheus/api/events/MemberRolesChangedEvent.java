package earth.terrarium.prometheus.api.events;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This event is fired when a players roles are updated.
 * This is fired when a role is added or removed from a player.
 * @param server The server that the roles were updated on.
 * @param player The player that had their roles updated.
 */
public record MemberRolesChangedEvent(MinecraftServer server, UUID player) {

    private static final List<Consumer<MemberRolesChangedEvent>> LISTENERS = new ArrayList<>();

    public static void register(Consumer<MemberRolesChangedEvent> listener) {
        LISTENERS.add(listener);
    }

    public static void unregister(Consumer<MemberRolesChangedEvent> listener) {
        LISTENERS.remove(listener);
    }

    @ApiStatus.Internal
    public static void fire(MemberRolesChangedEvent event) {
        LISTENERS.forEach(listener -> listener.accept(event));
    }
}
