package com.github.ageofwar.ragna.opengl.scene;

import com.github.ageofwar.ragna.*;
import com.github.ageofwar.ragna.opengl.GlModel;
import com.github.ageofwar.ragna.opengl.GlModels;
import com.github.ageofwar.ragna.opengl.GlShaderProgram;
import com.github.ageofwar.ragna.opengl.GlShaders;

import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.opengl.GL30.*;

public class Scene3D implements Scene {
    private final Content content;

    public Scene3D(Content content) {
        this.content = content;
    }

    @Override
    public void render(Window window, long time) {
        var shaderProgram3D = GlShaders.getShaderProgram3D();
        GlShaderProgram.bind(shaderProgram3D);

        var camera = content.camera(time);
        var aspectRatio = window.aspectRatio();
        shaderProgram3D.setUniformMatrix("viewMatrix", camera.matrix(aspectRatio));
        shaderProgram3D.setUniformVector("cameraPosition", camera.position().vector());

        var ambientLights = new ArrayList<Light.Ambient>();
        var pointLights = new ArrayList<Light.Point>();
        var directionalLights = new ArrayList<Light.Directional>();
        lights(camera, time, ambientLights, pointLights, directionalLights);
        shaderProgram3D.setUniform("ambientLightsSize", ambientLights.size());
        for (int i = 0; i < ambientLights.size(); i++) {
            shaderProgram3D.setUniform("ambientLights[" + i + "]", ambientLights.get(i));
        }
        shaderProgram3D.setUniform("directionalLightsSize", directionalLights.size());
        for (int i = 0; i < directionalLights.size(); i++) {
            shaderProgram3D.setUniform("directionalLights[" + i + "]", directionalLights.get(i));
        }
        shaderProgram3D.setUniform("pointLightsSize", pointLights.size());
        for (int i = 0; i < pointLights.size(); i++) {
            shaderProgram3D.setUniform("pointLights[" + i + "]", pointLights.get(i));
        }

        var solidEntities = new ArrayList<GlEntity>();
        var transparentEntities = new ArrayList<GlEntity>();
        entities(camera, time, solidEntities, transparentEntities);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (var entity : solidEntities) {
            entity.render();
        }

        glEnable(GL_CULL_FACE);
        for (var entity : transparentEntities) {
            glCullFace(GL_FRONT);
            entity.render();
            glCullFace(GL_BACK);
            entity.render();
        }

        window.render();
    }

    private void entities(Camera camera, long time, ArrayList<GlEntity> solidEntities, ArrayList<GlEntity> transparentEntities) {
        for (var entity : content.entities(camera, time)) {
            for (var model : entity.model()) {
                var glModel = GlModels.get(model);
                if (glModel.isTransparent()) {
                    transparentEntities.add(new GlEntity(entity, glModel));
                } else {
                    solidEntities.add(new GlEntity(entity, glModel));
                }
            }
        }
        depthSort(camera, transparentEntities);
    }

    private void lights(Camera camera, long time, ArrayList<Light.Ambient> ambientLights, ArrayList<Light.Point> pointLights, ArrayList<Light.Directional> directionalLights) {
        for (var light : content.lights(camera, time)) {
            if (light instanceof Light.Ambient ambientLight) {
                ambientLights.add(ambientLight);
            } else if (light instanceof Light.Point pointLight) {
                pointLights.add(pointLight);
            } else if (light instanceof Light.Directional directionalLight) {
                directionalLights.add(directionalLight);
            }
        }
    }

    private void depthSort(Camera camera, ArrayList<GlEntity> transparentEntities) {
        transparentEntities.sort(Comparator.comparing(entity -> -Vector.length(Matrix.productWithVector(Matrix.product(camera.viewMatrix()), entity.entity.position().vectorUniform()))));
    }

    private record GlEntity(Entity entity, GlModel model) {
        public void render() {
            model.render(entity.transformMatrix());
        }
    }

    public interface Content {
        Camera camera(long time);
        Iterable<Entity> entities(Camera camera, long time);
        Iterable<Light> lights(Camera camera, long time);
    }
}
