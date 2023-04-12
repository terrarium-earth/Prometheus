package earth.terrarium.prometheus.common.handlers.role;

import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.world.entity.player.Player;

public class RoleApiImpl implements RoleApi {

    @Override
    public <T extends RoleOption<T>> T getOption(Player player, RoleOptionSerializer<T> serializer) {
        if (player instanceof RoleEntityHook holder) {
            Role role = holder.prometheus$getHighestRole();
            if (role != null) {
                return role.getOption(serializer);
            }
        }
        return null;
    }
}
