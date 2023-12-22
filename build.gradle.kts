import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo

/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://jitpack.io")
    }
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.18")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("dev.arbjerg:lavaplayer:2.0.4")
    implementation("org.slf4j:slf4j-log4j12:2.0.9")
    implementation("se.michaelthelin.spotify:spotify-web-api-java:8.3.4")
    implementation("xyz.gianlu.librespot:librespot-lib:1.6.3")
    implementation("xyz.gianlu.librespot:librespot-player:1.6.3")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

group = "percyjw"
version = "0.9"
description = "Epic-Sounds-2-Project"
java.sourceCompatibility = JavaVersion.VERSION_17

application {
    mainClass.set("epicsounds2.main.Main")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("epic-sounds-2-0.9.jar")
    dependsOn(":distTar", ":distZip")
}