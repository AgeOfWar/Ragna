package com.github.ageofwar.solex.example;

import com.github.ageofwar.solex.Direction;
import com.github.ageofwar.solex.Position;
import com.github.ageofwar.solex.Rotation;
import com.github.ageofwar.solex.Window;

import static org.lwjgl.glfw.GLFW.*;

public class MovementFunction {
    private final Window window;
    private Position start;

    private long lastTime = System.nanoTime();

    public MovementFunction(Window window, Position start) {
        this.window = window;
        this.start = start;
    }

    public Position apply(long t, Position velocity, Rotation rotation) {
        var elapsedTime = (t - lastTime) / 1_000_000_000.0f;
        var relativeDirection = Direction.NONE;

        if (window.isKeyPressed(GLFW_KEY_W)) relativeDirection = relativeDirection.add(Direction.FORWARD);
        if (window.isKeyPressed(GLFW_KEY_S)) relativeDirection = relativeDirection.add(Direction.BACKWARD);
        if (window.isKeyPressed(GLFW_KEY_A)) relativeDirection = relativeDirection.add(Direction.LEFT);
        if (window.isKeyPressed(GLFW_KEY_D)) relativeDirection = relativeDirection.add(Direction.RIGHT);
        if (window.isKeyPressed(GLFW_KEY_SPACE)) relativeDirection = relativeDirection.add(Direction.UP);
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) relativeDirection = relativeDirection.add(Direction.DOWN);

        if (relativeDirection != Direction.NONE) {
            var direction = rotation.direction(relativeDirection);
            start = start.add(Position.fromVector(new float[] {
                    direction.x() * velocity.x() * elapsedTime,
                    direction.y() * velocity.y() * elapsedTime,
                    direction.z() * velocity.z() * elapsedTime
            }));
        }
        lastTime = t;
        return start;
    }

    public boolean isMoving() {
        return window.isKeyPressed(GLFW_KEY_W) || window.isKeyPressed(GLFW_KEY_S) ||
                window.isKeyPressed(GLFW_KEY_A) || window.isKeyPressed(GLFW_KEY_D) ||
                window.isKeyPressed(GLFW_KEY_SPACE) || window.isKeyPressed(GLFW_KEY_LEFT_SHIFT);
    }
}
