package earth.terrarium.prometheus.client.utils;

import net.minecraft.Util;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.InputStream;

public class SystemNotificationUtils {

    private static TrayIcon trayIcon;

    public static void init() {
        System.setProperty("java.awt.headless", "false"); //Client should NEVER be headless
        if (SystemTray.isSupported()) {
            try (InputStream icon = SystemNotificationUtils.class.getClassLoader().getResourceAsStream("tray_icon.png")) {
                if (icon == null) {
                    throw new NullPointerException("Icon is null");
                }
                byte[] bytes = IOUtils.toByteArray(icon);
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit.getDefaultToolkit().createImage(bytes);
                PopupMenu popup = new PopupMenu();
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.addActionListener(e -> System.exit(0));
                popup.add(exitItem);
                SystemNotificationUtils.trayIcon = new TrayIcon(image, "Prometheus", popup);
                SystemNotificationUtils.trayIcon.setImageAutoSize(true);
                tray.add(SystemNotificationUtils.trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendNotification(String notification, String title) {
        switch (Util.getPlatform()) {
            case WINDOWS -> sendTrayNotification(notification, title);
            case OSX -> sendMacNotification(notification, title);
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
                Runtime.getRuntime().exec(new String[]{"osascript", "-e", "display notification \"" + notification + "\" with title \"" + title + "\""});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
