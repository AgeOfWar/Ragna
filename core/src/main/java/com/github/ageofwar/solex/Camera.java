package com.github.ageofwar.solex;

public record Camera(Position position, Rotation rotation, Projection projection) {
    public Camera move(Position offset) {
        return new Camera(position.add(offset), rotation, projection);
    }

    public Camera move(float x, float y, float z) {
        return move(new Position(x, y, z));
    }

    public Camera rotate(Rotation offset) {
        return new Camera(position, rotation.add(offset), projection);
    }

    public Camera rotate(float roll, float pitch, float yaw) {
        return rotate(new Rotation(roll, pitch, yaw));
    }

    public Camera withRotation(Rotation rotation) {
        return new Camera(position, rotation, projection);
    }

    public Camera withProjection(Projection projection) {
        return new Camera(position, rotation, projection);
    }

    public Camera withPosition(Position position) {
        return new Camera(position, rotation, projection);
    }

    public float[] matrix(float aspectRatio) {
        return Matrix.product(projection.matrix(aspectRatio), rotation.oppositeMatrix(), position.oppositeMatrix());
    }

    public float[] viewMatrix() {
        return Matrix.product(rotation.matrix(), position.oppositeMatrix());
    }
}
