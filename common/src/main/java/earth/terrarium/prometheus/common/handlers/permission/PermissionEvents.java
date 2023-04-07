package earth.terrarium.prometheus.common.handlers.permission;

import net.minecraft.world.entity.Entity;

public class PermissionEvents {

    public static void onEntityJoin(Entity entity) {
        if (entity instanceof PermissionHolder holder) {
            holder.prometheus$updatePermissions();
        }
    }
}
