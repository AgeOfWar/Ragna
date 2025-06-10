package com.github.ageofwar.solex.opengl;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class GlTexture implements AutoCloseable {
    private final int id;

    private GlTexture(int id) {
        this.id = id;
    }

    public static GlTexture load(String path) {
        try (var stack = MemoryStack.stackPush()) {
            var widthBuffer = stack.mallocInt(1);
            var heightBuffer = stack.mallocInt(1);
            var channels = stack.mallocInt(1);

            var buffer = stbi_load(path, widthBuffer, heightBuffer, channels, 4);
            if (buffer == null) {
                throw new RuntimeException("Failed to load texture file: " + stbi_failure_reason());
            }
            var width = widthBuffer.get();
            var height = heightBuffer.get();

            var textureId = createTexture(width, height, buffer);

            stbi_image_free(buffer);
            return new GlTexture(textureId);
        }
    }

    private static int createTexture(int width, int height, ByteBuffer buffer) {
        var textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        return textureId;
    }

    public int id() {
        return id;
    }

    @Override
    public void close() {
        glDeleteTextures(id);
    }
}
