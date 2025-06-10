package com.github.ageofwar.solex;

import java.util.Objects;

public final class Color {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public static final Color WHITE = rgb(1, 1, 1);
    public static final Color BLACK = rgb(0, 0, 0);
    public static final Color GRAY = rgb(0.5f, 0.5f, 0.5f);
    public static final Color RED = rgb(1, 0, 0);
    public static final Color GREEN = rgb(0, 1, 0);
    public static final Color BLUE = rgb(0, 0, 1);
    public static final Color YELLOW = rgb(1, 1, 0);
    public static final Color CYAN = rgb(0, 1, 1);
    public static final Color MAGENTA = rgb(1, 0, 1);
    public static final Color TRANSPARENT = rgba(0, 0, 0, 0);
    public static final Color ORANGE = rgb(1, 0.5f, 0);

    public static Color rgba(float[] rgba) {
        return rgba(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public static Color rgba(float red, float green, float blue, float alpha) {
        return new Color(red, green, blue, alpha);
    }

    public static Color rgb(float red, float green, float blue) {
        return new Color(red, green, blue, 1);
    }

    public static Color gray(float gray) {
        return rgb(gray, gray, gray);
    }

    private Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float red() {
        return red;
    }

    public float green() {
        return green;
    }

    public float blue() {
        return blue;
    }

    public float alpha() {
        return alpha;
    }

    public Color withRed(float red) {
        return new Color(red, green, blue, alpha);
    }

    public Color withGreen(float green) {
        return new Color(red, green, blue, alpha);
    }

    public Color withBlue(float blue) {
        return new Color(red, green, blue, alpha);
    }

    public Color withAlpha(float alpha) {
        return new Color(red, green, blue, alpha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Color color)) return false;
        return Float.compare(color.red, red) == 0 && Float.compare(color.green, green) == 0 && Float.compare(color.blue, blue) == 0 && Float.compare(color.alpha, alpha) == 0;
    }

    @Override
    public String toString() {
        return "Color{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", alpha=" + alpha +
                '}';
    }
}
