package wtf.krypton.util;

import org.lwjgl.opengl.GL11;
import wtf.krypton.util.shader.impl.RoundedRectShader;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class DrawingUtil {

    private static final RoundedRectShader shader = new RoundedRectShader();

    public static void drawRect(float x, float y, float width, float height, int color) {
        color(color);

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x, y + height);

        GL11.glEnd();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        drawRoundedRect(x, y, width, height, radius, radius, radius, radius, 0, color);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float rtl, float rtr, float rbl, float rbr, int color) {
        drawRoundedRect(x, y, width, height, rtl, rtr, rbl, rbr, 0, color);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, float shadowBlur, int color) {
        drawRoundedRect(x, y, width, height, radius, radius, radius, radius, shadowBlur, color);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float rtl, float rtr, float rbl, float rbr, float shadowBlur, int color) {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        float a = (color >> 24 & 255) / 255.0F;

        shader.drawShader(x, y, width, height, rtl, rtr, rbl, rbr, shadowBlur, r, g, b, a);
    }

    public static void color(int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);
    }

}
