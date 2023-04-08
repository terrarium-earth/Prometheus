package earth.terrarium.prometheus.client.utils;

import net.minecraft.client.Minecraft;

public class ClientUtils {

    public static void sendCommand(String command) {
        Minecraft.getInstance().getConnection().sendUnsignedCommand(command);
    }
}
