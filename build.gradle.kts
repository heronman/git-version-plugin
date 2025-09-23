plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.agl.gradle"
version = VersionUtils.computeVersion(project)

gradlePlugin {
    plugins {
        create("versionPlugin") {
            id = "net.agl.gradle.version-plugin"
            implementationClass = "net.agl.gradle.VersionPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(17)
}

publishing {
    repositories {
        clear()
        maven {
            name = "agl-nexus"

            url = (
                    if (project.version.toString().contains("SNAPSHOT"))
                        (System.getProperty("repo.agl.snapshots")
                            ?: System.getenv("MAVEN_PUBLISH_SNAPSHOTS"))
                    else
                        (System.getProperty("repo.agl.snapshots")
                            ?: System.getenv("MAVEN_PUBLISH_RELEASES"))
                    ).let { project.uri(it) }

            credentials {
                username = System.getProperty("repo.agl.username") ?: System.getenv("MAVEN_PUBLISH_USERNAME")
                password = System.getProperty("repo.agl.password") ?: System.getenv("MAVEN_PUBLISH_PASSWORD")
            }
        }
    }
}
