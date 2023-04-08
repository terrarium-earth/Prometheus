package earth.terrarium.prometheus.common.handlers.permission;

import earth.terrarium.prometheus.common.handlers.role.RoleHolder;
import net.minecraft.world.entity.Entity;

public class PermissionEvents {

    public static void onEntityJoin(Entity entity) {
        if (entity instanceof PermissionHolder holder) {
            holder.prometheus$updatePermissions();
        }
        if (entity instanceof RoleHolder holder) {
            holder.prometheus$updateHighestRole();
        }
    }
}
