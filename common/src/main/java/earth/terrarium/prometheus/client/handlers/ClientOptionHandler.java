package earth.terrarium.prometheus.client.handlers;

import com.mojang.serialization.Codec;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.utils.SystemNotificationUtils;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Arrays;
import java.util.List;

public class ClientOptionHandler {

    public static OptionInstance<Boolean> showStuckButton;
    public static OptionInstance<Boolean> showNotifications;
    public static OptionInstance<NotificationHandler.PingSound> notificationSound;

    public static void onLoad() {
        showNotifications = OptionInstance.createBoolean(
            "options.prometheusShowNotifications",
            OptionInstance.cachedConstantTooltip(ConstantComponents.NOTIFICATION_OPTION_TOOLTIP),
            false,
            on -> {
                if (on && Screen.hasAltDown() && Screen.hasShiftDown()) {
                    String title = "Test Notification";
                    String message = "This is a test notification";
                    SystemNotificationUtils.sendNotification(message, title);
                }
            }
        );
        notificationSound = new OptionInstance<>(
            "options.prometheusNotificationSound",
            OptionInstance.cachedConstantTooltip(ConstantComponents.SOUND_OPTION_TOOLTIP),
            OptionInstance.forOptionEnum(),
            new OptionInstance.Enum<>(
                Arrays.asList(NotificationHandler.PingSound.values()),
                Codec.INT.xmap(NotificationHandler.PingSound::byId, NotificationHandler.PingSound::getId)
            ),
            NotificationHandler.PingSound.NONE,
            type -> {
                switch (type) {
                    case PING1 ->
                        Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(SoundEvent.createFixedRangeEvent(new ResourceLocation(Prometheus.MOD_ID, "ping_1"), 1.0F), 1.0F)
                        );
                    case PING2 ->
                        Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(SoundEvent.createFixedRangeEvent(new ResourceLocation(Prometheus.MOD_ID, "ping_2"), 1.0F), 1.0F)
                        );
                    case PING3 ->
                        Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(SoundEvent.createFixedRangeEvent(new ResourceLocation(Prometheus.MOD_ID, "ping_3"), 1.0F), 1.0F)
                        );
                }
            }
        );
        showStuckButton = OptionInstance.createBoolean(
            "options.prometheusShowStuckButton",
            OptionInstance.cachedConstantTooltip(ConstantComponents.STUCK_BUTTON_TOOLTIP),
            false,
            on -> {}
        );
    }

    public static void onParse(Options.FieldAccess access) {
        access.process("prometheusShowNotifications", showNotifications);
        access.process("prometheusNotificationSound", notificationSound);
        access.process("prometheusShowStuckButton", showStuckButton);
    }

    public static List<OptionInstance<?>> getChatOptions() {
        return List.of(showNotifications, notificationSound);
    }

    public static List<OptionInstance<?>> getAccessibilityOptions() {
        return List.of(showStuckButton);
    }

}
