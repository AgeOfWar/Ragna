package com.github.ageofwar.solex;

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
        var result = new float[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
        if (matrices.length == 0) {
            return result;
        }
        for (int i = 0; i < matrices.length; i++) {
            result = product(result, matrices[i]);
        }
        return result;
    }

    public static float[] product(float[] a, float[] b) {
        var result = new float[16];
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

    public static float[] productWithVector(float[] matrix, float[] vector) {
        var result = new float[4];
        for (int i = 0; i < 4; i++) {
            result[i] = 0;
            for (int j = 0; j < 4; j++) {
                result[i] += matrix[i * 4 + j] * vector[j];
            }
        }
        return result;
    }

    public static String toString(float[] matrix) {
        var builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                builder.append(matrix[i * 4 + j]).append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
