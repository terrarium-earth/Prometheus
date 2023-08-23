package earth.terrarium.prometheus.client.handlers.stuck;

import earth.terrarium.prometheus.client.handlers.ClientOptionHandler;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.GoSpawnPacket;
import earth.terrarium.prometheus.mixin.client.accessors.ProgressScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public class StuckRenderer {

    public static boolean shouldRender = false;

    public static List<Button> render(Screen screen, boolean isStuck) {
        if (shouldRender && screen instanceof StuckScreen stuckScreen) {
            stuckScreen.prometheus$setStuck(true);
        }
        if ((isStuck || shouldRender) && isCorrectScreen(screen) && ClientOptionHandler.showStuckButton.get()) {
            int width = Minecraft.getInstance().font.width(ConstantComponents.TP_PANIC);
            PlainTextButton button = new PlainTextButton(
                (screen.width - width) / 2, 4, width, 12,
                ConstantComponents.TP_PANIC,
                (b) -> NetworkHandler.CHANNEL.sendToServer(new GoSpawnPacket()), Minecraft.getInstance().font
            );
            return List.of(button);
        }
        return List.of();
    }

    private static boolean isCorrectScreen(Screen screen) {
        if (screen.getClass() == ReceivingLevelScreen.class) return true;
        return screen instanceof ProgressScreenAccessor accessor && Objects.equals(accessor.getHeader(), Component.translatable("connect.joining"));
    }
}
