package earth.terrarium.prometheus.api.roles.client;

import earth.terrarium.prometheus.common.handlers.role.Role;
import net.minecraft.client.gui.layouts.Layout;

public interface Page {

    Layout getContents(int width, int height);

    default boolean canSave() {
        return true;
    }

    default void save(Role role) {}
}
