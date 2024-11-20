package com.github.ageofwar.ragna.opengl.scene;

import com.github.ageofwar.ragna.Scene;
import com.github.ageofwar.ragna.Window;

public class SceneFrameLimit implements Scene {
    private final Scene scene;

    private final long minFrameTime;
    private long nextRenderTime;

    private SceneFrameLimit(Scene scene, long minFrameTime) {
        this.scene = scene;
        this.minFrameTime = minFrameTime;
        nextRenderTime = 0;
    }

    public static SceneFrameLimit maxFrameRate(Scene scene, int maxFrameRate) {
        return new SceneFrameLimit(scene, 1000000000L / maxFrameRate);
    }

    public static SceneFrameLimit minFrameTime(Scene scene, long minFrameTime) {
        return new SceneFrameLimit(scene, minFrameTime);
    }

    public static SceneFrameLimit unlimited(Scene scene) {
        return new SceneFrameLimit(scene, 0);
    }

    @Override
    public void render(Window window, long time) {
        if (minFrameTime == 0 || window.isResizing()) {
            scene.render(window, time);
            return;
        }

        if (time < nextRenderTime) {
            window.skipFrame();
            return;
        }

        scene.render(window, time);
        nextRenderTime = time + minFrameTime;
    }

    @Override
    public void close(Window window) {
        scene.close(window);
    }
}
