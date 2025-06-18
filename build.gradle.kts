plugins {
    id("java")
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
    implementation("com.google.code.gson:gson:2.13.1")
    paperweight.paperDevBundle("1.21.6-R0.1-SNAPSHOT") // dev bundle for paperweight, includes paper api
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}



// Mojang mapped output as plugin supports 1.21.5 and beyond
tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

// Copy output
tasks.register<Copy>("copyPluginJar") {
    // Specify the source of the file(s) to copy
    // This will take the JAR file produced by the 'jar' task
    from(tasks.jar.map { it.archiveFile })

    // Specify the destination directory
    // Example: Copy to a 'deploy' folder in the project root
    destinationDir = file("/home/kynes/MServer/plugins")

    // Ensure this task runs after the 'jar' task has completed
    dependsOn(tasks.jar)
}

// Make the 'build' task also execute your new 'copyPluginJar' task
tasks.build {
    dependsOn(tasks.named("copyPluginJar"))
}

