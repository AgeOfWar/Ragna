package com.github.ageofwar.solex;

public record Direction(float x, float y, float z) {
    public static final Direction NONE = new Direction(0, 0, 0);
    public static final Direction UP = new Direction(0, 1, 0);
    public static final Direction DOWN = new Direction(0, -1, 0);
    public static final Direction LEFT = new Direction(-1, 0, 0);
    public static final Direction RIGHT = new Direction(1, 0, 0);
    public static final Direction FORWARD = new Direction(0, 0, -1);
    public static final Direction BACKWARD = new Direction(0, 0, 1);

    public static Direction fromVector(float[] vector) {
        return new Direction(vector[0], vector[1], vector[2]);
    }

    public Direction {
        if (x != 0 || y != 0 || z != 0) {
            var length = (float) Math.sqrt(x * x + y * y + z * z);
            x /= length;
            y /= length;
            z /= length;
        }
    }

    public float[] vector() {
        return new float[]{x, y, z};
    }

    public float[] vectorUniform() {
        return new float[]{x, y, z, 0};
    }

    public Direction add(Direction direction) {
        return new Direction(x + direction.x, y + direction.y, z + direction.z);
    }
}
