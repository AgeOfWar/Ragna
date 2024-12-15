package com.github.ageofwar.ragna;

public sealed interface Material permits Material.Fill, Material.Texture, Material.SkyBox {
    record Fill(Color ambientColor, Color diffuseColor, Color specularColor, float reflectance, Color emissiveColor) implements Material {
        public Fill(Color color) {
            this(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, 0, color);
        }
    }

    record Texture(String path, float[] coordinates, Color ambientColor, Color diffuseColor, Color specularColor, float reflectance, Color emissiveColor) implements Material {
        public Texture(String path, float[] coordinates) {
            this(path, coordinates, Color.BLACK, Color.BLACK, Color.BLACK, 0, Color.BLACK);
        }
    }

    record SkyBox(String texturePath, float[] textureCoordinates, Color color, float intensity) implements Material {
        public SkyBox(String texturePath, float[] textureCoordinates) {
            this(texturePath, textureCoordinates, Color.WHITE, 1);
        }
    }
}
