package com.github.ageofwar.solex.opengl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL30.*;

public record GlShader(int shaderId) implements AutoCloseable {
    public static GlShader loadVertex(Path path) {
        try {
            return load(Files.readString(path), GL_VERTEX_SHADER);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static GlShader loadFragment(Path path) {
        try {
            return load(Files.readString(path), GL_FRAGMENT_SHADER);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static GlShader loadVertexFromResources(String resource) {
        return load(readResource(resource), GL_VERTEX_SHADER);
    }

    public static GlShader loadFragmentFromResources(String resource) {
        return load(readResource(resource), GL_FRAGMENT_SHADER);
    }

    private static GlShader load(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        return new GlShader(shaderId);
    }

    private static String readResource(String resource) {
        try (var inputStream = GlShader.class.getClassLoader().getResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resource);
            }
            var result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        glDeleteShader(shaderId);
    }
}
