package com.github.ageofwar.solex;

public record Entity(Model[] model, Position position, Rotation rotation, Scale scale) implements Renderable {
    public Entity(Model[] model, Position position, Rotation rotation) {
        this(model, position, rotation, Scale.ONE);
    }

    public Entity(Model[] model, Position position) {
        this(model, position, Rotation.ZERO, Scale.ONE);
    }

    public Entity(Model model, Position position, Rotation rotation, Scale scale) {
        this(new Model[]{ model }, position, rotation, scale);
    }

    public float[] transformMatrix() {
        return Matrix.product(position.matrix(), rotation.matrix(), scale.matrix());
    }
}
