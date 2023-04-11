package earth.terrarium.prometheus.common.handlers.role;

import java.util.UUID;

public record RoleEntry(UUID id, Role role) {

    public boolean isDefault() {
        return id.equals(DefaultRole.DEFAULT_ROLE);
    }
}
