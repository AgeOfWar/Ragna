package com.github.ageofwar.solex;

public record Position(float x, float y, float z) {
    public static final Position ORIGIN = new Position(0, 0, 0);
    public static final Position ZERO = new Position(0, 0, 0);

    public static Position fromVector(float[] vector) {
        return new Position(vector[0], vector[1], vector[2]);
    }

    public float[] vectorUniform() {
        return new float[]{x, y, z, 1};
    }

    public float[] vector() {
        return new float[]{x, y, z};
    }

    public Position add(Position position) {
        return new Position(x + position.x, y + position.y, z + position.z);
    }

    public Position add(Direction direction, float distance) {
        return new Position(x + direction.x() * distance, y + direction.y() * distance, z + direction.z() * distance);
    }

    public Position subtract(Position position) {
        return new Position(x - position.x, y - position.y, z - position.z);
    }

    public float distance(Position position) {
        return (float) Math.sqrt(Math.pow(x - position.x, 2) + Math.pow(y - position.y, 2) + Math.pow(z - position.z, 2));
    }

    public Direction directionTo(Position position) {
        return new Direction(position.x - x, position.y - y, position.z - z);
    }

    public Position scale(float factor) {
        return new Position(x * factor, y * factor, z * factor);
    }

    public Position rotate(Rotation rotation) {
        return Position.fromVector(Matrix.productWithVector(rotation.matrix(), vectorUniform()));
    }

    public float[] matrix() {
        return new float[]{
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1
        };
    }

    public float[] oppositeMatrix() {
        return new float[]{
                1, 0, 0, -x,
                0, 1, 0, -y,
                0, 0, 1, -z,
                0, 0, 0, 1
        };
    }
}
