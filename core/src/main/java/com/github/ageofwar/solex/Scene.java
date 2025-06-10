package com.github.ageofwar.solex;

public interface Scene {
    void render(Window window, long time);
    default void close(Window window) {
    }
}
