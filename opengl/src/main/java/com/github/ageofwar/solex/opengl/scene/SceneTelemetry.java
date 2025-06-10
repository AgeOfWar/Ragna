package com.github.ageofwar.solex.opengl.scene;

import com.github.ageofwar.solex.Scene;
import com.github.ageofwar.solex.Window;

import java.util.DoubleSummaryStatistics;
import java.util.LongSummaryStatistics;

public class SceneTelemetry implements Scene {
    private final Scene scene;
    private long lastTime;
    private LongSummaryStatistics frameTime = new LongSummaryStatistics();
    private DoubleSummaryStatistics fps = new DoubleSummaryStatistics();

    public SceneTelemetry(Scene scene) {
        this.scene = scene;
        lastTime = System.nanoTime();
    }

    @Override
    public void render(Window window, long time) {
        scene.render(window, time);
        if (window.shouldRender()) {;
            var deltaTime = time - lastTime;
            lastTime = time;
            frameTime.accept(deltaTime);
            fps.accept(1e9 / deltaTime);
        }
    }

    @Override
    public void close(Window window) {
        scene.close(window);
    }

    public void reset() {
        frameTime = new LongSummaryStatistics();
        fps = new DoubleSummaryStatistics();
    }

    public DoubleSummaryStatistics getFps() {
        return fps;
    }

    public LongSummaryStatistics getFrameTime() {
        return frameTime;
    }
}
