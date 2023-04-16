package earth.terrarium.prometheus.client.handlers;

import com.mojang.serialization.Codec;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;

import java.util.Arrays;
import java.util.List;

public class ClientOptionHandler {

    public static OptionInstance<Boolean> showNotifications;
    public static OptionInstance<NotificationHandler.PingSound> notificationSound;

    public static void onLoad() {
        showNotifications = OptionInstance.createBoolean(
            "options.prometheusShowNotifications",
            OptionInstance.cachedConstantTooltip(ConstantComponents.NOTIFICATION_OPTION_TOOLTIP),
            false
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
            ignored -> {
                //TODO play sound
            }
        );
    }

    public static void onParse(Options.FieldAccess access) {
        access.process("prometheusShowNotifications", showNotifications);
        access.process("prometheusNotificationSound", notificationSound);
    }

    public static List<OptionInstance<?>> getChatOptions() {
        return List.of(showNotifications, notificationSound);
    }

}
