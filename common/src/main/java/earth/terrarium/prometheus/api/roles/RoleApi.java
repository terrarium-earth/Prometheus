package earth.terrarium.prometheus.api.roles;

import earth.terrarium.prometheus.api.ApiHelper;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface RoleApi {

    RoleApi API = ApiHelper.load(RoleApi.class);

    @Nullable
    <T extends RoleOption<T>> T getOption(Player player, RoleOptionSerializer<T> serializer);
}
