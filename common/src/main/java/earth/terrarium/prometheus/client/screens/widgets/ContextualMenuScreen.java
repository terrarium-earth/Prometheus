package earth.terrarium.prometheus.client.screens.widgets;

import net.minecraft.client.Minecraft;

import java.util.Optional;

public interface ContextualMenuScreen {

    ContextMenu getContextMenu();

    static Optional<ContextMenu> getMenu() {
        if (Minecraft.getInstance().screen instanceof ContextualMenuScreen screen) {
            return Optional.ofNullable(screen.getContextMenu());
        }
        return Optional.empty();
    }
}
