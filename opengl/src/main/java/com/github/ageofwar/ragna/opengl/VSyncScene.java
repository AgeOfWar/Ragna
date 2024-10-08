package com.github.ageofwar.ragna.opengl;

import com.github.ageofwar.ragna.Scene;
import com.github.ageofwar.ragna.Window;

import static org.lwjgl.glfw.GLFW.*;

public class VSyncScene implements Scene {
    private final Scene scene;

    private VSyncScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void init(Window window) {
        glfwSwapInterval(1);
    }

    @Override
    public void render(Window window) {
        scene.render(window);
    }

    @Override
    public void close(Window window) {
        glfwSwapInterval(0);
    }
}
