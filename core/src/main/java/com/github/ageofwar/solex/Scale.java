package com.github.ageofwar.solex;

public record Scale(float x, float y, float z) {
    public static final Scale ZERO = new Scale(0);
    public static final Scale ONE = new Scale(1);

    public Scale(float factor) {
        this(factor, factor, factor);
    }

    public float[] matrix() {
        return new float[] {
                x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1
        };
    }

    public float[] inverseMatrix() {
        return new float[] {
                1 / x, 0, 0, 0,
                0, 1 / y, 0, 0,
                0, 0, 1 / z, 0,
                0, 0, 0, 1
        };
    }
}
