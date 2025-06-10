package com.github.ageofwar.solex.opengl;

import com.github.ageofwar.solex.Engine;
import com.github.ageofwar.solex.Scene;
import com.github.ageofwar.solex.Window;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class GlWindow implements Window {
    private final Engine engine;
    private final long id;
    private boolean resizing;
    private Scene scene;
    private long lastRenderTime;
    private boolean skipFrame = true;

    private final ArrayList<Runnable> closeCallbacks = new ArrayList<>();
    private final ArrayList<KeyCallback> keyCallbacks = new ArrayList<>();
    private final ArrayList<MouseButtonCallback> mouseButtonCallbacks = new ArrayList<>();
    private final ArrayList<CursorPositionCallback> cursorPositionCallbacks = new ArrayList<>();
    private final ArrayList<CursorPositionCallback> cursorPositionRelativeCallbacks = new ArrayList<>();

    public GlWindow(long id, Engine engine) {
        this.id = id;
        this.engine = Objects.requireNonNull(engine);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(id);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private void swapBuffers() {
        glfwSwapBuffers(id);
    }

    private void makeContextCurrent() {
        glfwMakeContextCurrent(id);
    }

    @Override
    public void setKeyCallback(KeyCallback callback) {
        Objects.requireNonNull(callback);
        keyCallbacks.add(callback);
    }

    @Override
    public void setCloseCallback(Runnable callback) {
        Objects.requireNonNull(callback);
        closeCallbacks.add(callback);
    }

    @Override
    public void setMouseButtonCallback(MouseButtonCallback callback) {
        Objects.requireNonNull(callback);
        mouseButtonCallbacks.add(callback);
    }

    @Override
    public void setCursorPositionCallback(CursorPositionCallback callback) {
        Objects.requireNonNull(callback);
        cursorPositionCallbacks.add(callback);
    }

    @Override
    public void setCursorPositionRelativeCallback(CursorPositionCallback callback) {
        Objects.requireNonNull(callback);
        cursorPositionRelativeCallbacks.add(callback);
    }

    public void init() {
        makeContextCurrent();
        GL.createCapabilities();
        glfwWindowHint(GLFW_SAMPLES, 8);
        glEnable(GL_MULTISAMPLE);
        // GLUtil.setupDebugMessageCallback();
        glClearColor(0, 0, 0, 0);
        var viewport = viewportSize();
        glViewport(0, 0, viewport.width(), viewport.height());
        setVSync(false);
        glfwSetWindowRefreshCallback(id, (window) -> {
            var viewportSize = viewportSize();
            glViewport(0, 0, viewportSize.width(), viewportSize.height());
            resizing = true;
            render(lastRenderTime);
            resizing = false;
        });
        initCallbacks();
    }

    private void initCallbacks() {
        glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> {
            for (var keyCallback : keyCallbacks) {
                keyCallback.invoke(key, scancode, action, mods);
            }
        });
        glfwSetWindowCloseCallback(id, (window) -> {
            for (var closeCallback : closeCallbacks) {
                closeCallback.run();
            }
        });
        glfwSetMouseButtonCallback(id, (window, button, action, mods) -> {
            for (var mouseButtonCallback : mouseButtonCallbacks) {
                mouseButtonCallback.invoke(button, action, mods);
            }
        });
        glfwSetCursorPosCallback(id, (window, x, y) -> {
            for (var cursorPositionCallback : cursorPositionCallbacks) {
                cursorPositionCallback.invoke(x, y);
            }
            for (var cursorPositionRelativeCallback : cursorPositionRelativeCallbacks) {
                var viewportSize = viewportSize();
                cursorPositionRelativeCallback.invoke(x / viewportSize.width(), y / viewportSize.height());
            }
        });
    }

    public void render(long time) {
        lastRenderTime = time;
        makeContextCurrent();
        if (scene != null) scene.render(this, time);
        if (!skipFrame) swapBuffers();
        skipFrame = true;
    }

    @Override
    public void close() {
        glfwFreeCallbacks(id);
        if (scene != null) scene.close(this);
        glfwDestroyWindow(id);
    }

    @Override
    public Size viewportSize() {
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetFramebufferSize(id, width, height);
        return new Size(width[0], height[0]);
    }

    @Override
    public float aspectRatio() {
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetFramebufferSize(id, width, height);
        return (float) width[0] / height[0];
    }

    @Override
    public boolean isResizing() {
        return resizing;
    }

    @Override
    public boolean isKeyPressed(int key) {
        return glfwGetKey(id, key) == GLFW_PRESS;
    }

    @Override
    public CursorPosition cursorPosition() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(id, x, y);
        return new CursorPosition(x[0], y[0]);
    }

    @Override
    public CursorPosition cursorPositionRelative() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(id, x, y);
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetFramebufferSize(id, width, height);
        return new CursorPosition(x[0] / width[0], y[0] / height[0]);
    }

    @Override
    public void render() {
        skipFrame = false;
    }

    @Override
    public boolean shouldRender() {
        return !skipFrame;
    }

    @Override
    public void setVSync(boolean vSync) {
        glfwSwapInterval(vSync ? 1 : 0);
    }

    @Override
    public void setTitle(String title) {
        glfwSetWindowTitle(id, title);
    }

    @Override
    public Engine engine() {
        return engine;
    }

    public long id() {
        return id;
    }
}
