package earth.terrarium.prometheus.api.permissions;

import earth.terrarium.prometheus.api.ApiHelper;
import earth.terrarium.prometheus.api.TriState;
import net.minecraft.world.entity.player.Player;

public interface PermissionApi {

    PermissionApi API = ApiHelper.load(PermissionApi.class);

    TriState getPermission(Player player, String permission);
}
