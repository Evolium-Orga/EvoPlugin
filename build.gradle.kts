plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.palmus.evoplugin"
version = "1.2.1"

val RepoUsername = providers.gradleProperty("repsyUsername")
val RepoPassword = providers.gradleProperty("repsyPassword")

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
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://jitpack.io") }

    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.0.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.3")
}

tasks.shadowJar {
    with(this) {
        configurations = listOf(project.configurations.shadow.get())
    }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }

        repositories {
            maven {
                url = uri("https://repo.repsy.io/mvn/palmus/evoplugin")
                credentials {
                    username = RepoUsername.orNull
                    password = RepoPassword.orNull
                }
            }
        }
    }
}