package com.github.ageofwar.solex.example;

import com.github.ageofwar.solex.Rotation;
import com.github.ageofwar.solex.Window;

import java.util.function.LongFunction;

import static org.lwjgl.glfw.GLFW.*;

class RotationFunction implements LongFunction<Rotation>, Window.MouseButtonCallback, Window.CursorPositionCallback {
    private final Window window;
    private Rotation start;
    private final Rotation velocity;
    private Window.CursorPosition dragStart;
    private Rotation current;

    public RotationFunction(Window window, Rotation start, Rotation velocity) {
        this.window = window;
        this.start = start;
        this.velocity = velocity;
        current = start;
        window.setMouseButtonCallback(this);
        window.setCursorPositionRelativeCallback(this);
    }

    private Rotation newRotation(double x, double y) {
        var pitch = (float) (x - dragStart.x()) * velocity.pitch();
        var roll = (float) (y - dragStart.y()) * velocity.roll();
        var newRotation = start.add(new Rotation(roll, pitch, 0));
        if (newRotation.roll() > Math.PI / 2) newRotation = newRotation.withRoll((float) (Math.PI / 2));
        if (newRotation.roll() < -Math.PI / 2) newRotation = newRotation.withRoll((float) (-Math.PI / 2));
        return newRotation;
    }

    @Override
    public Rotation apply(long value) {
        return current;
    }

    @Override
    public void invoke(double x, double y) {
        if (dragStart == null) return;
        current = newRotation(x, y);
    }

    @Override
    public void invoke(int button, int action, int mods) {
        if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_1) {
            dragStart = window.cursorPositionRelative();
        }
        if (action == GLFW_RELEASE && button == GLFW_MOUSE_BUTTON_1) {
            var pos = window.cursorPositionRelative();
            start = newRotation(pos.x(), pos.y());
            dragStart = null;
        }
    }
}
