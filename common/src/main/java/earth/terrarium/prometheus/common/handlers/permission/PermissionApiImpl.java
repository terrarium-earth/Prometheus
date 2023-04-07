package earth.terrarium.prometheus.common.handlers.permission;

import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import net.minecraft.world.entity.player.Player;

public class PermissionApiImpl implements PermissionApi {

    @Override
    public TriState getPermission(Player player, String permission) {
        if (player instanceof PermissionHolder holder) {
            if (holder.prometheus$getPermissions() == null) {
                holder.prometheus$updatePermissions();
            }
            return holder.prometheus$hasPermission(permission);
        }
        return TriState.UNDEFINED;
    }
}
