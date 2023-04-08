package earth.terrarium.prometheus.client.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public record MouseLocationFix(double x, double y, Class<?> screen, long time) {

    private static MouseLocationFix fix = null;

    public static void fix(Class<?> screen) {
        if (fix != null && fix.screen() == screen && System.currentTimeMillis() - fix.time() < 1000) {
            GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(), fix.x(), fix.y());
            fix = null;
        }
    }

    public static void setFix(Class<?> screen) {
        double x = Minecraft.getInstance().mouseHandler.xpos();
        double y = Minecraft.getInstance().mouseHandler.ypos();
        fix = new MouseLocationFix(x, y, screen, System.currentTimeMillis());
    }
}
