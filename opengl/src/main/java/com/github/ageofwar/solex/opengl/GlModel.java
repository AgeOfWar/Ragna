package com.github.ageofwar.solex.opengl;

import com.github.ageofwar.solex.Model;

import static org.lwjgl.opengl.GL30.*;

public class GlModel implements AutoCloseable {
    private final int objectId;
    private final int positionsBufferId;
    private final int indicesBufferId;
    private final int normalsBufferId;
    private final int vertices;
    private final Material material;

    public static GlModel create(Model model) {
        var objectId = glGenVertexArrays();
        glBindVertexArray(objectId);

        var positionsBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionsBufferId);
        glBufferData(GL_ARRAY_BUFFER, model.mesh().vertices(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glVertexAttrib2fv(1, new float[] {-1, -1});
        glDisableVertexAttribArray(1);
        glVertexAttrib2fv(2, new float[] {-1, -1});
        glDisableVertexAttribArray(2);
        glVertexAttrib2fv(3, new float[] {-1, -1});
        glDisableVertexAttribArray(3);
        glVertexAttrib2fv(4, new float[] {-1, -1});
        glDisableVertexAttribArray(4);

        var normalsBufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, normalsBufferId);
        glBufferData(GL_ARRAY_BUFFER, model.mesh().normals(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        var material = Material.from(model.material());
        material.create(model);

        var indicesBufferId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, model.mesh().indices(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
        return new GlModel(objectId, positionsBufferId, indicesBufferId, normalsBufferId, model.mesh().numVertices(), material);
    }

    private GlModel(int objectId, int positionsBufferId, int indicesBufferId, int normalsBufferId, int vertices, Material material) {
        this.objectId = objectId;
        this.positionsBufferId = positionsBufferId;
        this.indicesBufferId = indicesBufferId;
        this.normalsBufferId = normalsBufferId;
        this.vertices = vertices;
        this.material = material;
    }

    public void render(float[] transformMatrix) {
        glBindVertexArray(objectId);
        material.prerender(transformMatrix);
        glDrawElements(GL_TRIANGLES, vertices, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void close() {
        material.close();
        glDeleteBuffers(indicesBufferId);
        glDeleteBuffers(positionsBufferId);
        glDeleteBuffers(normalsBufferId);
        glDeleteVertexArrays(objectId);
    }

    public boolean isTransparent() {
        return material.isTransparent();
    }

    private interface Material {
        static Material from(com.github.ageofwar.solex.Material material) {
            if (material instanceof com.github.ageofwar.solex.Material.Fill fill) {
                return new Fill2(fill);
            }
            throw new IllegalArgumentException();
        }

        void create(Model model);
        void prerender(float[] transformMatrix);
        void close();
        boolean isTransparent();

        class Fill2 implements Material {
            private final com.github.ageofwar.solex.Material.Fill fill;
            private GlTexture ambientTexture;
            private GlTexture diffuseTexture;
            private GlTexture specularTexture;
            private GlTexture emissiveTexture;
            private int ambientTextureBufferId;
            private int diffuseTextureBufferId;
            private int specularTextureBufferId;
            private int emissiveTextureBufferId;

            public Fill2(com.github.ageofwar.solex.Material.Fill fill) {
                this.fill = fill;
            }

            @Override
            public void create(Model model) {
                var ambientTexture = fill.ambient().texture();
                var diffuseTexture = fill.diffuse().texture();
                var specularTexture = fill.specular().texture();
                var emissiveTexture = fill.emissive().texture();
                if (ambientTexture != null) {
                    this.ambientTexture = GlTexture.load(ambientTexture.path());
                    ambientTextureBufferId = glGenBuffers();
                    glBindBuffer(GL_ARRAY_BUFFER, ambientTextureBufferId);
                    glBufferData(GL_ARRAY_BUFFER, ambientTexture.coordinates(), GL_STATIC_DRAW);
                    glEnableVertexAttribArray(1);
                    glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
                    glBindBuffer(GL_ARRAY_BUFFER, 0);
                }
                if (diffuseTexture != null) {
                    this.diffuseTexture = GlTexture.load(diffuseTexture.path());
                    diffuseTextureBufferId = glGenBuffers();
                    glBindBuffer(GL_ARRAY_BUFFER, diffuseTextureBufferId);
                    glBufferData(GL_ARRAY_BUFFER, diffuseTexture.coordinates(), GL_STATIC_DRAW);
                    glEnableVertexAttribArray(2);
                    glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
                    glBindBuffer(GL_ARRAY_BUFFER, 0);
                }
                if (specularTexture != null) {
                    this.specularTexture = GlTexture.load(specularTexture.path());
                    specularTextureBufferId = glGenBuffers();
                    glBindBuffer(GL_ARRAY_BUFFER, specularTextureBufferId);
                    glBufferData(GL_ARRAY_BUFFER, specularTexture.coordinates(), GL_STATIC_DRAW);
                    glEnableVertexAttribArray(3);
                    glVertexAttribPointer(3, 2, GL_FLOAT, false, 0, 0);
                    glBindBuffer(GL_ARRAY_BUFFER, 0);
                }
                if (emissiveTexture != null) {
                    this.emissiveTexture = GlTexture.load(emissiveTexture.path());
                    emissiveTextureBufferId = glGenBuffers();
                    glBindBuffer(GL_ARRAY_BUFFER, emissiveTextureBufferId);
                    glBufferData(GL_ARRAY_BUFFER, emissiveTexture.coordinates(), GL_STATIC_DRAW);
                    glEnableVertexAttribArray(4);
                    glVertexAttribPointer(4, 2, GL_FLOAT, false, 0, 0);
                    glBindBuffer(GL_ARRAY_BUFFER, 0);
                }
            }

            @Override
            public void prerender(float[] transformMatrix) {
                var shaderProgram = GlShaders.getShaderProgram3D();
                GlShaderProgram.bind(shaderProgram);
                if (ambientTexture != null) {
                    glActiveTexture(GL_TEXTURE0);
                    glBindTexture(GL_TEXTURE_2D, ambientTexture.id());
                }
                if (diffuseTexture != null) {
                    glActiveTexture(GL_TEXTURE1);
                    glBindTexture(GL_TEXTURE_2D, diffuseTexture.id());
                }
                if (specularTexture != null) {
                    glActiveTexture(GL_TEXTURE2);
                    glBindTexture(GL_TEXTURE_2D, specularTexture.id());
                }
                if (emissiveTexture != null) {
                    glActiveTexture(GL_TEXTURE3);
                    glBindTexture(GL_TEXTURE_2D, emissiveTexture.id());
                }
                shaderProgram.setUniformMatrix("modelMatrix", transformMatrix);
                shaderProgram.setUniform("material.ambient", fill.ambient().color());
                shaderProgram.setUniform("material.diffuse", fill.diffuse().color());
                shaderProgram.setUniform("material.specular", fill.specular().color());
                shaderProgram.setUniform("material.emissive", fill.emissive().color());
                shaderProgram.setUniform("material.reflectance", fill.specular().reflectance());
                shaderProgram.setUniform("material.specularPower", fill.specular().specularPower());
            }

            @Override
            public void close() {
                if (ambientTexture != null) {
                    glDeleteBuffers(ambientTextureBufferId);
                    ambientTexture.close();
                }
                if (diffuseTexture != null) {
                    glDeleteBuffers(diffuseTextureBufferId);
                    diffuseTexture.close();
                }
                if (specularTexture != null) {
                    glDeleteBuffers(specularTextureBufferId);
                    specularTexture.close();
                }
                if (emissiveTexture != null) {
                    glDeleteBuffers(emissiveTextureBufferId);
                    emissiveTexture.close();
                }
            }

            @Override
            public boolean isTransparent() {
                return false;
            }
        }
    }
}
