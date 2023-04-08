package earth.terrarium.prometheus.common.handlers.role;

public interface RoleHolder {

    void prometheus$updateHighestRole();

    Role prometheus$getHighestRole();
}
