plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta17"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17" // for NMS internals
}

group = "dev.tom.sentinels"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.google.code.gson:gson:2.13.1")
    paperweight.paperDevBundle("1.21.6-R0.1-SNAPSHOT") // dev bundle for paperweight, includes paper api
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}



// Mojang mapped output as plugin supports 1.21.5 and beyond
tasks.shadowJar {
    relocate("org.reflections", "dev.tom.sentinels.shadow.org.reflections")
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

// Copy output
tasks.register<Copy>("copyPluginJar") {
    val shadowJar = tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar")
    dependsOn(shadowJar)

    from(shadowJar.map { it.archiveFile.get().asFile })
    into("/home/kynes/MServer/plugins") // target plugin folder
}

// Make the 'build' task also execute your new 'copyPluginJar' task
tasks.build {
    dependsOn(tasks.named("copyPluginJar"))
}

