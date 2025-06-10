package com.github.ageofwar.solex.example;

import com.github.ageofwar.solex.Scene;
import com.github.ageofwar.solex.Window;
import com.github.ageofwar.solex.WindowConfiguration;
import com.github.ageofwar.solex.opengl.GlEngine;
import com.github.ageofwar.solex.opengl.scene.Scene3D;
import com.github.ageofwar.solex.opengl.scene.SceneFrameLimit;
import com.github.ageofwar.solex.opengl.scene.SceneTelemetry;

public class Main {
    public static final int FPS = 156;

    public static void main(String[] args) {
        var mainThread = Thread.currentThread();
        var windowConfiguration = new WindowConfiguration("Hello World!", 800, 800);
        try (var engine = GlEngine.create()) {
            var window = engine.createWindow(windowConfiguration);
            window.setScene(setupScene(window));
            window.setCloseCallback(mainThread::interrupt);
            engine.run();
        }
    }

    public static Scene setupScene(Window window) {
        var content = new SceneContent(window);
        var scene = new Scene3D(content);
        var frameLimit = SceneFrameLimit.maxFrameRate(scene, FPS);
        var telemetry = new SceneTelemetry(frameLimit);
        window.engine().asyncExecutor().scheduleAtFixedRate(() -> {
           window.setTitle(content.windowTitle(telemetry.getFps().getAverage()));
           telemetry.reset();
        }, 500000000, 500000000);
        return telemetry;
    }
}
