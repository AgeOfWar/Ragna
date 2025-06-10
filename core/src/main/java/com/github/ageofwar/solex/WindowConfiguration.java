package com.github.ageofwar.solex;

public record WindowConfiguration(String title, int width, int height) {
    public WindowConfiguration {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
    }
}
