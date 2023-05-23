package earth.terrarium.prometheus.api.roles;

import earth.terrarium.prometheus.api.ApiHelper;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public interface RoleApi {

    RoleApi API = ApiHelper.load(RoleApi.class);

    @Nullable <T extends RoleOption<T>> T getOption(Player player, RoleOptionSerializer<T> serializer);

    /**
     * Note: This will recalculate the role each time this is called so use it for operations that are not performance critical.
     */
    @Nullable <T extends RoleOption<T>> T forceGetOption(Level level, UUID player, RoleOptionSerializer<T> serializer);

    /**
     * Returns the option for the player, or throws an exception if the option is null.
     * Should only be used if an option has a default value.
     */
    default <T extends RoleOption<T>> T getNonNullOption(Player player, RoleOptionSerializer<T> serializer) {
        return Objects.requireNonNull(getOption(player, serializer), "Option is null");
    }

    /**
     * Returns the option for the player with uuid, or throws an exception if the option is null.
     * Should only be used if an option has a default value.
     * <br>
     * Note: This will recalculate the role each time this is called so use it for operations that are not performance critical.
     */
    default <T extends RoleOption<T>> T forceGetNonNullOption(Level level, UUID player, RoleOptionSerializer<T> serializer) {
        return Objects.requireNonNull(forceGetOption(level, player, serializer), "Option is null");
    }
}
