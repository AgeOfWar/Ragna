package com.github.ageofwar.solex.opengl;

public class GlShaders {
    private static GlShader vertexShader3D;
    private static GlShader fragmentShader3D;

    private static GlShaderProgram shaderProgram3D;

    private GlShaders() {
    }

    public static GlShader getVertexShader3D() {
        if (vertexShader3D == null) {
            vertexShader3D = GlShader.loadVertexFromResources("shaders/3D.vert");
        }
        return vertexShader3D;
    }

    public static GlShader getFragmentShader3D() {
        if (fragmentShader3D == null) {
            fragmentShader3D = GlShader.loadFragmentFromResources("shaders/3D.frag");
        }
        return fragmentShader3D;
    }

    public static GlShaderProgram getShaderProgram3D() {
        if (shaderProgram3D == null) {
            shaderProgram3D = GlShaderProgram.create(getVertexShader3D(), getFragmentShader3D());
            GlShaderProgram.bind(shaderProgram3D);
            shaderProgram3D.setUniform("ambientTextureSampler", 0);
            shaderProgram3D.setUniform("diffuseTextureSampler", 1);
            shaderProgram3D.setUniform("specularTextureSampler", 2);
            shaderProgram3D.setUniform("emissiveTextureSampler", 3);
        }
        return shaderProgram3D;
    }

    public static void close() {
        if (shaderProgram3D != null) {
            shaderProgram3D.close();
        }
        if (vertexShader3D != null) {
            vertexShader3D.close();
        }
        if (fragmentShader3D != null) {
            fragmentShader3D.close();
        }
    }
}
