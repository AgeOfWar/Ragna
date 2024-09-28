package com.github.ageofwar.ragna;

public final class Matrix {
    private Matrix() {
    }

    public static float[] transpose(float[] matrix) {
        return new float[] {
                matrix[0], matrix[4], matrix[8], matrix[12],
                matrix[1], matrix[5], matrix[9], matrix[13],
                matrix[2], matrix[6], matrix[10], matrix[14],
                matrix[3], matrix[7], matrix[11], matrix[15]
        };
    }

    public static float[] product(float[]... matrices) {
        if (matrices.length == 0) {
            return new float[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
        }
        float[] result = matrices[0];
        for (int i = 1; i < matrices.length; i++) {
            result = product(result, matrices[i]);
        }
        return result;
    }

    public static float[] product(float[] a, float[] b) {
        float[] result = new float[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i * 4 + j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i * 4 + j] += a[i * 4 + k] * b[k * 4 + j];
                }
            }
        }
        return result;
    }

    public static String toString(float[] matrix) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                builder.append(matrix[i * 4 + j]).append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
