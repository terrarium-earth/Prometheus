package earth.terrarium.prometheus.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientUtils {

    public static void setTooltip(Component component) {
        if (Minecraft.getInstance().screen != null) {
            Minecraft.getInstance().screen.setTooltipForNextRenderPass(List.of(component.getVisualOrderText()));
        }
    }

    public static void sendCommand(String command) {
        Minecraft.getInstance().getConnection().sendUnsignedCommand(command);
    }

    public static void sendClick(AbstractContainerScreen<?> screen, int content) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(screen.getMenu().containerId, content);
        }
    }
}
