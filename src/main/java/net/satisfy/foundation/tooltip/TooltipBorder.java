package net.satisfy.foundation.tooltip;

/**
 * Hacky but works: mark a color right before a tooltip renders and the
 * mixin picks it up for the border. Mark expires after 100ms or after one use.
 */
public final class TooltipBorder {
    public static final int SOUL_TOP = 0xF040C8E8;
    public static final int SOUL_BOTTOM = 0xF01A5C8C;
    public static final int HOT_TOP = 0xF0E8603C;
    public static final int HOT_BOTTOM = 0xF08C2414;
    private static final long TIMEOUT = 100L;
    private static long markedAt;
    private static int top;
    private static int bottom;

    private TooltipBorder() {
    }

    /** Next tooltip border uses these colors (ARGB). */
    public static void mark(int topColor, int bottomColor) {
        top = topColor;
        bottom = bottomColor;
        markedAt = System.currentTimeMillis();
    }

    /** Blueish soul fire border. */
    public static void markSoul() {
        mark(SOUL_TOP, SOUL_BOTTOM);
    }

    /** Orange/red hot border. */
    public static void markHot() {
        mark(HOT_TOP, HOT_BOTTOM);
    }

    public static int top(int color) {
        return isMarked() ? top : color;
    }

    public static int bottom(int color) {
        if (!isMarked()) {
            return color;
        }
        markedAt = 0L;
        return bottom;
    }

    private static boolean isMarked() {
        return System.currentTimeMillis() - markedAt < TIMEOUT;
    }
}
