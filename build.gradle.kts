plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.evolium.evoshop"
version = "1.0"

repositories {
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/central") }
    maven { url = uri("https://maven.citizensnpcs.co/repo") }
    maven { url = uri("https://repo.repsy.io/mvn/palmus/evoplugin") }

    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    implementation("fr.palmus.evoplugin:EvoPlugin:1.2.1")
    compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT") {
        exclude("*","*")
    }

}

tasks.shadowJar {
    with(this) {
        configurations = listOf(project.configurations.shadow.get())
    }
}

tasks.test {
    useJUnitPlatform()
}
