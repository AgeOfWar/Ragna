package com.github.ageofwar.ragna;

public record PerspectiveProjection(float fov, float near, float far) implements Projection {
    @Override
    public float[] projectionMatrix(float aspectRatio) {
        return new float[]{
                (float) (1 / Math.tan(fov / 2) / aspectRatio), 0, 0, 0,
                0, (float) (1 / Math.tan(fov / 2)), 0, 0,
                0, 0, (far + near) / (near - far), 2 * far * near / (near - far),
                0, 0, -1, 0
        };
    }
}
