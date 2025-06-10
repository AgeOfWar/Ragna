import org.lwjgl.Lwjgl
import org.lwjgl.Release
import org.lwjgl.lwjgl

plugins {
    id("application")
    id("org.lwjgl.plugin") version "0.0.35"
}

application {
    mainClass.set("com.github.ageofwar.solex.example.Main")
}

group = "com.github.ageofwar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":opengl"))

    lwjgl {
        version = Release.latest
        implementation(Lwjgl.Preset.minimalOpenGL)
    }
}

tasks.jar {
    dependsOn(configurations.runtimeClasspath)

    duplicatesStrategy = DuplicatesStrategy.WARN

    manifest {
        attributes(mapOf("Main-Class" to application.mainClass))
    }

    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
}

