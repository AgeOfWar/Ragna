package com.github.ageofwar.solex.example;

import com.github.ageofwar.solex.*;

public record Planet(Model[] model, Color color, float radius, float distance, float period, Rotation orbitRotation, Rotation inclination, float day) {
    public static final Planet SUN = new Planet(ModelLoader.load("assets/planets/sun/sphere.obj"), Color.WHITE, 696340f, 0, 0, Rotation.ZERO, Rotation.ZERO, 25.38f);
    public static final Planet MERCURY = new Planet(ModelLoader.load("assets/planets/mercury/Mercury.fbx"), Color.GRAY, 2439f, 57.9f, 88f, new Rotation(0, 0.84351763f, 0.1224f), new Rotation(0, 0, 0.1224f), 58.6f);
    public static final Planet VENUS = new Planet(ModelLoader.load("assets/planets/venus/Venus.fbx"), Color.YELLOW, 6051f, 108.2f, 224.7f, new Rotation(0, 1.3383185f, 0.0592f), new Rotation(0, 0, 0.0592f), 243f);
    public static final Planet EARTH = new Planet(ModelLoader.load("assets/planets/earth/Earth.fbx"), Color.BLUE, 6371f, 147f, 365.25f, new Rotation(0, 0, 0), new Rotation(0, 0, 0.4091f), 1);
    public static final Planet MARS = new Planet(ModelLoader.load("assets/planets/mars/Mars.fbx"), Color.RED, 3389f, 206.6f, 687f, new Rotation(0, 0.8650f, 0.0323f), new Rotation(0, 0, 0.0323f), 1.03f);
    public static final Planet JUPITER = new Planet(ModelLoader.load("assets/planets/jupiter/Jupiter.fbx"), Color.ORANGE, 69911f, 740.5f, 4331f, new Rotation(0, 1.7541f, 0.0229f), new Rotation(0, 0, 0.0229f), 0.41f);
    public static final Planet SATURN = new Planet(ModelLoader.load("assets/planets/saturn/Saturn.fbx"), Color.YELLOW, 58232f, 1352.6f, 10747f, new Rotation(0, 1.9847f, 0.0434f), new Rotation(0, 0, 0.0434f), 0.45f);
    public static final Planet URANUS = new Planet(ModelLoader.load("assets/planets/uranus/Uranus.fbx"), Color.CYAN, 25362f, 2741.3f, 30589f, new Rotation(0, 1.2915f, 0.0134f), new Rotation(0, 0, 0.0134f), 0.72f);
    public static final Planet NEPTUNE = new Planet(ModelLoader.load("assets/planets/neptune/Neptune.fbx"), Color.BLUE, 24624f, 4444.5f, 59800f, new Rotation(0, 2.2994f, 0.0309f), new Rotation(0, 0, 0.0309f), 0.67f);
    public static final Planet PLUTO = new Planet(ModelLoader.load("assets/planets/pluto/Pluto.fbx"), Color.WHITE, 1188f, 4436.8f, 90560f, new Rotation(0, 1.9249f, 0.2993f), new Rotation(0, 0, 0.2993f), 6.39f);

    public Position position(long time) {
        if (this == SUN) return Position.ORIGIN;
        var distance = this.distance * 1e6f;
        var t = time / 1000000000f;
        return new Position(distance * (float) Math.cos(2 * Math.PI * t / period), 0, distance * (float) Math.sin(2 * Math.PI * t / period)).rotate(orbitRotation);
    }

    public Rotation rotation(long time) {
        var t = time / 1000000000f;
        return new Rotation(0, 2 * (float) Math.PI * t / day, 0).add(inclination);
    }
}
