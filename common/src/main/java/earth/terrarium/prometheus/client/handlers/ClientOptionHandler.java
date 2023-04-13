package earth.terrarium.prometheus.client.handlers;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;

import java.util.List;

public class ClientOptionHandler {

    public static OptionInstance<Boolean> showNotifications;

    public static void onLoad() {
        showNotifications = OptionInstance.createBoolean("options.prometheusShowNotifications", false);
    }

    public static void onParse(Options.FieldAccess access) {
        access.process("prometheusShowNotifications", showNotifications);
    }

    public static List<OptionInstance<?>> getChatOptions() {
        return List.of(showNotifications);
    }

}
