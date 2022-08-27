package wtf.krypton.util;

public class HoveredUtil {

	public static boolean isTextHovered(float textWidth, float textHeight, float x, float y, float mouseX, float mouseY) {
		return mouseX >= x - 2 && mouseX <= x + textWidth + 2 && mouseY <= y + textHeight && mouseY >= y;
	}
	
	public static boolean isRectHovered(float x, float y, float width, float height, float mouseX, float mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY <= y + height && mouseY >= y;
	}

	public static boolean isCircleHovered(float x, float y, float radius, float mouseX, float mouseY) {
		return MathUtil.getDistance(x, y, mouseX, mouseY) <= radius;
	}
	
}
