import org.lwjgl.*

plugins {
    id("java-library")
    id("application")
    id("org.lwjgl.plugin") version "0.0.35"
}

application {
    mainClass.set("com.github.ageofwar.ragna.Main")
}

group = "com.github.ageofwar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    sonatype()
}

dependencies {
    api(project(":core"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    lwjgl {
        version = Release.latest
        implementation(Lwjgl.Preset.minimalOpenGL)
        implementation(Lwjgl.Addons.`joml 1․10․7`)
        implementation(Lwjgl.Module.assimp)
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    dependsOn(configurations.runtimeClasspath)

    duplicatesStrategy = DuplicatesStrategy.WARN

    manifest {
        attributes(mapOf("Main-Class" to application.mainClass))
    }

    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
}
