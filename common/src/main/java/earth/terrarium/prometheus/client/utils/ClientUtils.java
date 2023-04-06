package earth.terrarium.prometheus.client.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ClientUtils {

    private static final long DEFAULT_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
    private static final long POINTING_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR);
    private static final long DISABLED_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_NOT_ALLOWED_CURSOR);
    private static final long TEXT_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);

    public static void setPointing() {
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), ClientUtils.POINTING_CURSOR);
    }

    public static void setDisabled() {
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), ClientUtils.DISABLED_CURSOR);
    }

    public static void setText() {
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), ClientUtils.TEXT_CURSOR);
    }

    public static void setDefault() {
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), ClientUtils.DEFAULT_CURSOR);
    }
}
