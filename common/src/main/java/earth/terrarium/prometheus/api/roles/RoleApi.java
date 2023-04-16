package earth.terrarium.prometheus.api.roles;

import earth.terrarium.prometheus.api.ApiHelper;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface RoleApi {

    RoleApi API = ApiHelper.load(RoleApi.class);

    @Nullable <T extends RoleOption<T>> T getOption(Player player, RoleOptionSerializer<T> serializer);

    /**
     * Returns the option for the player, or throws an exception if the option is null.
     * Should only be used if an option has a default value.
     */
    default <T extends RoleOption<T>> T getNonNullOption(Player player, RoleOptionSerializer<T> serializer) {
        return Objects.requireNonNull(getOption(player, serializer), "Option is null");
    }
}
