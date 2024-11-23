package com.github.ageofwar.ragna;

public record Entity(Model model, Position position, Rotation rotation, Scale scale) {
    public Entity(Model model, Position position, Rotation rotation) {
        this(model, position, rotation, Scale.ONE);
    }

    public Entity(Model model, Position position) {
        this(model, position, Rotation.ZERO, Scale.ONE);
    }

    public float[] transformMatrix() {
        return Matrix.product(position.matrix(), rotation.matrix(), scale.matrix());
    }
}
