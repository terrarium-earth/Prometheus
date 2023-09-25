package earth.terrarium.prometheus.client.utils;

import net.minecraft.Util;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class SystemNotificationUtils {

    private static final List<BiFunction<String, String, Boolean>> LINUX_RUNS = List.of(
        SystemNotificationUtils::sendGnomeNotification,
        SystemNotificationUtils::sendKdeNotifcation
    );

    private static TrayIcon trayIcon;

    public static void init() {
        // We cant trust the OS to be able to handle the tray icon on linux or mac
        if (Util.getPlatform() == Util.OS.OSX) return;
        if (Util.getPlatform() == Util.OS.LINUX) return;
        System.setProperty("java.awt.headless", "false"); //Client should NEVER be headless
        if (SystemTray.isSupported()) {
            try (InputStream icon = SystemNotificationUtils.class.getClassLoader().getResourceAsStream("prometheus_icon.png")) {
                if (icon == null) {
                    throw new NullPointerException("Icon is null");
                }
                byte[] bytes = IOUtils.toByteArray(icon);
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage(bytes);
                SystemNotificationUtils.trayIcon = new TrayIcon(image, "Prometheus");
                SystemNotificationUtils.trayIcon.setImageAutoSize(true);
                tray.add(SystemNotificationUtils.trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.setProperty("java.awt.headless", "true");
    }

    public static void sendNotification(String notification, String title) {
        switch (Util.getPlatform()) {
            case OSX -> sendMacNotification(notification, title);
            case LINUX -> sendLinuxNotification(notification, title);
            default -> sendTrayNotification(notification, title);
        }
    }

    private static boolean sendTrayNotification(String notification, String title) {
        if (SystemNotificationUtils.trayIcon == null) return false;
        SystemNotificationUtils.trayIcon.displayMessage(title, notification, TrayIcon.MessageType.NONE);
        return true;
    }

    private static void sendMacNotification(String notification, String title) {
        if (Util.getPlatform() != Util.OS.OSX) return;
        if (!sendTrayNotification(notification, title)) {
            try {
                Runtime.getRuntime().exec(new String[]{
                    "osascript",
                    "-e",
                    "display notification \"" + notification + "\" with title \"" + title + "\""
                });
            } catch (Exception ignored) {
            }
        }
    }

    private static void sendLinuxNotification(String notification, String title) {
        if (Util.getPlatform() != Util.OS.LINUX) return;
        if (!sendTrayNotification(notification, title)) {
            for (var run : LINUX_RUNS) {
                if (run.apply(notification, title)) return;
            }
        }
    }

    private static boolean sendGnomeNotification(String notification, String title) {
        try {
            var process = Runtime.getRuntime().exec(new String[]{
                "notify-send",
                "\"" + title + "\"",
                "\"" + notification + "\""
            });
            if (process == null) return false;
            return !process.waitFor(1, TimeUnit.SECONDS) || process.exitValue() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean sendKdeNotifcation(String notification, String title) {
        try {
            var process = Runtime.getRuntime().exec(new String[]{
                "kdialog",
                "--title",
                "\"" + title + "\"",
                "--passivepopup",
                "\"" + notification + "\"",
                "3"
            });
            if (process == null) return false;
            return !process.waitFor(1, TimeUnit.SECONDS) || process.exitValue() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }
}
