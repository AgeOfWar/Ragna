package com.github.ageofwar.solex;

public record Rotation(float roll, float pitch, float yaw) {
    public static final Rotation ZERO = new Rotation(0, 0, 0);

    public static Rotation random() {
        return new Rotation((float) (Math.random() * 2 * Math.PI), (float) (Math.random() * 2 * Math.PI), (float) (Math.random() * 2 * Math.PI));
    }

    public Rotation add(Rotation rotation) {
        return new Rotation(roll + rotation.roll, pitch + rotation.pitch, yaw + rotation.yaw);
    }

    public Rotation add(float roll, float pitch, float yaw) {
        return add(new Rotation(roll, pitch, yaw));
    }

    public Rotation normalized() {
        return new Rotation(roll % (2 * (float) Math.PI), pitch % (2 * (float) Math.PI), yaw % (2 * (float) Math.PI));
    }

    public Rotation withRoll(float roll) {
        return new Rotation(roll, pitch, yaw);
    }

    public Rotation withPitch(float pitch) {
        return new Rotation(roll, pitch, yaw);
    }

    public Rotation withYaw(float yaw) {
        return new Rotation(roll, pitch, yaw);
    }

    public float[] matrix() {
        float cosRoll = (float) Math.cos(roll);
        float sinRoll = (float) Math.sin(roll);
        float cosPitch = (float) Math.cos(pitch);
        float sinPitch = (float) Math.sin(pitch);
        float cosYaw = (float) Math.cos(yaw);
        float sinYaw = (float) Math.sin(yaw);
        return new float[]{
                cosPitch * cosYaw, sinRoll * sinPitch * cosYaw - cosRoll * sinYaw, cosRoll * sinPitch * cosYaw + sinRoll * sinYaw, 0,
                cosPitch * sinYaw, sinRoll * sinPitch * sinYaw + cosRoll * cosYaw, cosRoll * sinPitch * sinYaw - sinRoll * cosYaw, 0,
                -sinPitch, sinRoll * cosPitch, cosRoll * cosPitch, 0,
                0, 0, 0, 1
        };
    }

    public float[] oppositeMatrix() {
        float cosRoll = (float) Math.cos(roll);
        float sinRoll = (float) Math.sin(roll);
        float cosPitch = (float) Math.cos(pitch);
        float sinPitch = (float) Math.sin(pitch);
        float cosYaw = (float) Math.cos(yaw);
        float sinYaw = (float) Math.sin(yaw);
        return new float[]{
                cosPitch * cosYaw, cosPitch * sinYaw, -sinPitch, 0,
                sinRoll * sinPitch * cosYaw - cosRoll * sinYaw, sinRoll * sinPitch * sinYaw + cosRoll * cosYaw, sinRoll * cosPitch, 0,
                cosRoll * sinPitch * cosYaw + sinRoll * sinYaw, cosRoll * sinPitch * sinYaw - sinRoll * cosYaw, cosRoll * cosPitch, 0,
                0, 0, 0, 1
        };
    }

    public Direction direction(Direction relativeDirection) {
        return Direction.fromVector(Matrix.productWithVector(matrix(), relativeDirection.vectorUniform()));
    }
}
