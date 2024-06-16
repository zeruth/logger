plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

group = "nulled"
version = "1.1"

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
            val propjectDIr = project.layout.projectDirectory
            url = uri("file://$propjectDIr/.nulled-repo")
        }
    }
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}