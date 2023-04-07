package earth.terrarium.prometheus.common.handlers.permission;

import earth.terrarium.prometheus.api.TriState;

import java.util.Map;

public interface PermissionHolder {

    void prometheus$updatePermissions();

    TriState prometheus$hasPermission(String permission);

    Map<String, TriState> prometheus$getPermissions();
}
