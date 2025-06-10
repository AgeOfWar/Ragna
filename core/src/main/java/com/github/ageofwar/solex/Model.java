package com.github.ageofwar.solex;

public record Model(Mesh mesh, Material material) {
    public Model withMesh(Mesh mesh) {
        return new Model(mesh, material);
    }

    public Model withMaterial(Material material) {
        return new Model(mesh, material);
    }
}
