package earth.terrarium.prometheus.common.handlers.role;

public interface RoleEntityHook {

    void prometheus$updateHighestRole();

    Role prometheus$getHighestRole();
}
