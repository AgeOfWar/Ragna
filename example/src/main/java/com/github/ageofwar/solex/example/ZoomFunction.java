package com.github.ageofwar.solex.example;

import com.github.ageofwar.solex.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

public class ZoomFunction {
    private final Window window;
    private final float withoutZoom;
    private final float withZoom;
    private final long zoomDuration;

    private boolean beforeZoom = false;
    private long zoomStartTime;

    public ZoomFunction(Window window, float withoutZoom, float withZoom, long zoomDuration) {
        this.window = window;
        this.withoutZoom = withoutZoom;
        this.withZoom = withZoom;
        this.zoomDuration = zoomDuration;
    }

    public float apply(long time) {
        var shouldZoom = window.isKeyPressed(GLFW_KEY_Z);
        if (shouldZoom && !beforeZoom) {
            zoomStartTime = time;
        }
        var fov = withoutZoom;
        if (shouldZoom) {
            var zoomTime = time - zoomStartTime;
            fov = withoutZoom + (withZoom - withoutZoom) * Math.min(zoomTime, zoomDuration) / zoomDuration;
        }
        beforeZoom = shouldZoom;
        return fov;
    }

}
