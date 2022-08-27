package wtf.krypton.util;

import org.lwjgl.glfw.GLFW;

public class GLFWUtil {

    public static long windowID;

    public static boolean isWindowFocused() {
        return GLFW.glfwGetWindowAttrib(windowID, GLFW.GLFW_FOCUSED) == 1;
    }

}
