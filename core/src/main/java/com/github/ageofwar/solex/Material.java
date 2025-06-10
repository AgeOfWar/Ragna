package com.github.ageofwar.solex;

public sealed interface Material permits Material.Fill {
    record Fill(Ambient ambient, Diffuse diffuse, Specular specular, Emissive emissive) implements Material {
        public Fill(Ambient ambient) {
            this(ambient, new Diffuse(Color.TRANSPARENT), new Specular(Color.TRANSPARENT, 0, 1), new Emissive(Color.TRANSPARENT));
        }

        public Fill(Diffuse diffuse) {
            this(new Ambient(Color.TRANSPARENT), diffuse, new Specular(Color.TRANSPARENT, 0, 1), new Emissive(Color.TRANSPARENT));
        }

        public Fill(Specular specular) {
            this(new Ambient(Color.TRANSPARENT), new Diffuse(Color.TRANSPARENT), specular, new Emissive(Color.TRANSPARENT));
        }

        public Fill(Emissive emissive) {
            this(new Ambient(Color.TRANSPARENT), new Diffuse(Color.TRANSPARENT), new Specular(Color.TRANSPARENT, 0, 1), emissive);
        }
    }

    record Ambient(Color color, com.github.ageofwar.solex.Texture texture) {
        public Ambient(Color color) {
            this(color, null);
        }

        public Ambient(com.github.ageofwar.solex.Texture texture) {
            this(Color.TRANSPARENT, texture);
        }
    }

    record Diffuse(Color color, com.github.ageofwar.solex.Texture texture) {
        public Diffuse(Color color) {
            this(color, null);
        }

        public Diffuse(com.github.ageofwar.solex.Texture texture) {
            this(Color.TRANSPARENT, texture);
        }
    }

    record Specular(Color color, com.github.ageofwar.solex.Texture texture, float reflectance, float specularPower) {
        public Specular(Color color, float reflectance, float specularPower) {
            this(color, null, reflectance, specularPower);
        }

        public Specular(com.github.ageofwar.solex.Texture texture, float reflectance, float specularPower) {
            this(Color.TRANSPARENT, texture, reflectance, specularPower);
        }
    }

    record Emissive(Color color, com.github.ageofwar.solex.Texture texture) {
        public Emissive(Color color) {
            this(color, null);
        }

        public Emissive(com.github.ageofwar.solex.Texture texture) {
            this(Color.TRANSPARENT, texture);
        }
    }
}
