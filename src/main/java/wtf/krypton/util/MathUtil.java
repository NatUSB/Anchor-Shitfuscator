package wtf.krypton.util;

public class MathUtil {

    private static final double[] a;
    private static final double[] b;

    static {
        a = new double[65536];
        b = new double[360];
        for (int i = 0; i < 65536; ++i) {
            MathUtil.a[i] = Math.sin(i * 3.141592653589793 * 2.0 / 65536.0);
        }
        for (int i = 0; i < 360; ++i) {
            MathUtil.b[i] = Math.sin(Math.toRadians(i));
        }
    }

    public static double interpolate(final double now, final double then, final double percent) {
        return (now + (then - now) * percent);
    }

    public static float getPercentByMaxAndMin(float input, float min, float max) {
        return ((input - min)) / (max - min);
    }

    public static float snap(float value, float min, float max, float snapAmount) {
        return Math.max(min, Math.min(Math.round(value / snapAmount) * snapAmount, max));
    }

    public static float getRandomInRange(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.abs(Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
    }

    public static float clamp(float num, float min, float max){
        if(num > max) return max;
        if(num < min) return min;
        return num;
    }

    public static double getAngle(int nameInt) {
        nameInt %= 360;
        return MathUtil.b[nameInt];
    }

    public static double getRightAngle(int nameInt) {
        nameInt += 90;
        nameInt %= 360;
        return MathUtil.b[nameInt];
    }

    public static int ceiling_float_int(float value) {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }

    public static int ceiling_double_int(double value) {
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }

}
