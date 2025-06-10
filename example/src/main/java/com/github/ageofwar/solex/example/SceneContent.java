package com.github.ageofwar.solex.example;

import com.github.ageofwar.solex.*;
import com.github.ageofwar.solex.opengl.scene.Scene3D;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class SceneContent implements Scene3D.Content {
    private static final Model[] skybox = ModelLoader.load("assets/skybox/model.obj");
    private static final Mesh point = ModelLoader.load("assets/point/sphere.obj")[0].mesh();
    private static final Model ship = new Model(ModelLoader.load("assets/ship/ship.obj")[0].mesh(), new Material.Fill(
            new Material.Ambient(Color.rgba(0.5f, 1f, 0.5f, 0.4f)),
            new Material.Diffuse(Color.rgba(0.5f, 1f, 0.5f, 1f)),
            new Material.Specular(Color.GREEN, 0.5f, 5),
            new Material.Emissive(Color.TRANSPARENT)
    ));

    private static final float SKYBOX_SIZE = 100;
    private static final float MIN_HELP_PLANET_RADIUS = SKYBOX_SIZE / 300;

    private final Window window;
    private final Planet[] planets = new Planet[]{
            Planet.SUN,
            Planet.MERCURY,
            Planet.VENUS,
            Planet.EARTH,
            Planet.MARS,
            Planet.JUPITER,
            Planet.SATURN,
            Planet.URANUS,
            Planet.NEPTUNE,
            Planet.PLUTO
    };
    private final MovementFunction movementFunction;
    private final RotationFunction rotationFunction;
    private final ZoomFunction zoomFunction;
    private long startSimulationTime = System.nanoTime();
    private float nearestPlanetDistance = Float.MAX_VALUE;
    private long lastTime = startSimulationTime;
    private Position lastPosition = new Position(0, 0, 100);
    private boolean paused = false;
    private boolean helpMode = false;

    private final SoundPlayer engineSound = new SoundPlayer();
    private boolean wasMoving = false;

    public SceneContent(Window window) {
        this.window = window;
        movementFunction = new MovementFunction(window, new Position(0, 0, 100));
        rotationFunction = new RotationFunction(window, Rotation.ZERO, new Rotation(4, 4, 4));
        zoomFunction = new ZoomFunction(window, (float) Math.toRadians(50), (float) Math.toRadians(5), 500000000);
        window.setKeyCallback((key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ENTER && action == GLFW_RELEASE) {
                paused = !paused;
                if (!paused) {
                    startSimulationTime += System.nanoTime() - lastTime;
                }
            }
            if (key == GLFW_KEY_H && action == GLFW_RELEASE) {
                helpMode = !helpMode;
            }
        });
    }

    @Override
    public Camera camera(long time) {
        var rotation = rotationFunction.apply(time);
        var velocity = getCameraVelocity(nearestPlanetDistance);
        var position = movementFunction.apply(time, velocity, rotation);
        var fov = zoomFunction.apply(time);
        return new Camera(position, rotation, new PerspectiveProjection(fov, 0.001f, 174f));
    }

    @Override
    public Iterable<? extends Renderable> render(Camera camera, long time) {
        // Reset simulation time if 'R' is pressed
        if (window.isKeyPressed(GLFW_KEY_R)) startSimulationTime = System.nanoTime();

        // Update camera position and rotation
        if (!paused) lastTime = time;
        lastPosition = camera.position();
        nearestPlanetDistance = Float.MAX_VALUE; // Slow down camera near planets
        var t = lastTime - startSimulationTime;
        var entities = new ArrayList<Renderable>();

        for (var planet : planets) {
            var position = planet.position(t);

            var virtualPosition = position.scale(1e-6f);
            var virtualRadius = planet.radius() * 1e-6f;
            nearestPlanetDistance = Math.min(nearestPlanetDistance, camera.position().distance(virtualPosition) - planet.radius() * 1e-6f);

            // Fix occlusion by skybox
            if (virtualPosition.distance(camera.position()) > SKYBOX_SIZE) {
                if (!helpMode)
                    virtualRadius = virtualRadius * SKYBOX_SIZE / camera.position().distance(virtualPosition);
                virtualPosition = camera.position().add(camera.position().directionTo(virtualPosition), SKYBOX_SIZE);
            }

            if (helpMode) {
                entities.add(new Entity(helpModel(planet), virtualPosition, planet.rotation(t), new Scale(Math.max(virtualRadius, MIN_HELP_PLANET_RADIUS))));
            } else {
                entities.add(new Entity(planet.model(), virtualPosition, planet.rotation(t), new Scale(virtualRadius)));
            }

            if (planet == Planet.SUN) {
                entities.add(new Light.Point(position, Color.WHITE, 1f, new Light.Attenuation(0.7f, 0.01f, 0)));
            }
        }

        entities.add(new Entity(skybox, camera.position(), Rotation.ZERO, new Scale(SKYBOX_SIZE)));
        entities.add(new Light.Ambient(Color.WHITE, 0.02f));
        handleShip(entities, camera);

        return entities;
    }

    public void handleShip(List<Renderable> entities, Camera camera) {
        if (nearestPlanetDistance > 0.05f) {
            entities.add(new Entity(
                    ship,
                    camera.position().add(camera.rotation().direction(Direction.FORWARD), 0.04f).add(new Position(0, -0.01f, 0)),
                    new Rotation(-camera.rotation().roll(), (float) Math.PI + camera.rotation().pitch(), camera.rotation().yaw()),
                    new Scale(0.001f)
            ));
            var isMoving = movementFunction.isMoving();

            if (isMoving && !wasMoving) {
                engineSound.start();
            } else if (!isMoving && wasMoving) {
                engineSound.stop();
            }
            wasMoving = isMoving;

            if (isMoving) {
                entities.add(new Entity(
                        new Model(point, new Material.Fill(new Material.Emissive(Color.rgba(0.616f, 0, 1, 1)))),
                        camera.position().add(camera.rotation().direction(Direction.FORWARD), 0.037f).add(new Position(0, -0.00990f, 0)),
                        Rotation.ZERO,
                        new Scale(0.0005f)
                ));
            }
        } else if (wasMoving) {
            engineSound.stop();
            wasMoving = false;
        }
    }

    public Model helpModel(Planet planet) {
        return new Model(point, new Material.Fill(new Material.Emissive(planet.color())));
    }

    private Position getCameraVelocity(float nearestPlanetDistance) {
        if (nearestPlanetDistance > 250) return new Position(1000, 1000, 1000);
        if (nearestPlanetDistance > 25) return new Position(100, 100, 100);
        if (nearestPlanetDistance > 2.5) return new Position(10, 10, 10);
        if (nearestPlanetDistance > 0.25) return new Position(1, 1, 1);
        if (nearestPlanetDistance > 0.025) return new Position(0.1f, 0.1f, 0.1f);
        return new Position(0.05f, 0.05f, 0.05f);
    }

    public String windowTitle(double fps) {
        var time = Instant.ofEpochMilli((lastTime - startSimulationTime) / 1_000_000 * 60 * 60 * 24);
        var title = time.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (paused) {
            title += " (paused)";
        }
        title += " - " + lastPosition;
        title += " - " + String.format("%.0f", fps) + " FPS";
        return title;
    }
}
