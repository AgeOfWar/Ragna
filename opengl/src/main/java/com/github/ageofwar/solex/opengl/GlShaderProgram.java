package com.github.ageofwar.solex.opengl;

import com.github.ageofwar.solex.Color;
import com.github.ageofwar.solex.Light;

import static org.lwjgl.opengl.GL30.*;

public record GlShaderProgram(int id) implements AutoCloseable {
    private static int currentProgram = 0;

    public static void bind(GlShaderProgram program) {
        if (currentProgram != program.id) {
            glUseProgram(program.id);
            currentProgram = program.id;
        }
    }

    public static void unbind() {
        glUseProgram(0);
    }

    public static GlShaderProgram create() {
        var programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }
        return new GlShaderProgram(programId);
    }

    public static GlShaderProgram create(GlShader... shaders) {
        var program = create();
        for (var shader : shaders) {
            glAttachShader(program.id, shader.shaderId());
        }
        program.link();
        for (var shader : shaders) {
            program.detach(shader);
        }
        return program;
    }

    public void setUniform(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    public void setUniform(String name, float value) {
        glUniform1f(glGetUniformLocation(id, name), value);
    }

    public void setUniformMatrix(String name, float[] value) {
        glUniformMatrix4fv(glGetUniformLocation(id, name), true, value);
    }

    public void setUniformVector(String name, float[] value) {
        glUniform3fv(glGetUniformLocation(id, name), value);
    }

    public void setUniform(String name, Color value) {
        glUniform4f(glGetUniformLocation(id, name), value.red(), value.green(), value.blue(), value.alpha());
    }

    public void setUniformRGB(String name, Color value) {
        glUniform3f(glGetUniformLocation(id, name), value.red(), value.green(), value.blue());
    }

    public void setUniform(String name, Light.Ambient value) {
        glUniform3f(glGetUniformLocation(id, name + ".color"), value.color().red(), value.color().green(), value.color().blue());
        glUniform1f(glGetUniformLocation(id, name + ".intensity"), value.intensity());
    }

    public void setUniform(String name, Light.Directional value) {
        glUniform3f(glGetUniformLocation(id, name + ".color"), value.color().red(), value.color().green(), value.color().blue());
        glUniform1f(glGetUniformLocation(id, name + ".intensity"), value.intensity());
        glUniform3f(glGetUniformLocation(id, name + ".direction"), value.direction().x(), value.direction().y(), value.direction().z());
    }

    public void setUniform(String name, Light.Point value) {
        glUniform3f(glGetUniformLocation(id, name + ".color"), value.color().red(), value.color().green(), value.color().blue());
        glUniform3f(glGetUniformLocation(id, name + ".position"), value.position().x(), value.position().y(), value.position().z());
        glUniform1f(glGetUniformLocation(id, name + ".intensity"), value.intensity());
        glUniform1f(glGetUniformLocation(id, name + ".attenuation.constant"), value.attenuation().constant());
        glUniform1f(glGetUniformLocation(id, name + ".attenuation.linear"), value.attenuation().linear());
        glUniform1f(glGetUniformLocation(id, name + ".attenuation.quadratic"), value.attenuation().quadratic());
    }

    public void link() {
        glLinkProgram(id);
        if (glGetProgrami(id, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(id, 1024));
        }
    }

    public void detach(GlShader shader) {
        glDetachShader(id, shader.shaderId());
    }

    public void validate() {
        glValidateProgram(id);
        if (glGetProgrami(id, GL_VALIDATE_STATUS) == 0) {
            throw new RuntimeException("Error validating Shader code: " + glGetProgramInfoLog(id, 1024));
        }
    }

    @Override
    public void close() {
        if (id != 0) {
            glDeleteProgram(id);
        }
    }
}