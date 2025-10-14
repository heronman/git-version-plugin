plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.agl.gradle"
version = versionFromGit(project)

gradlePlugin {
    plugins {
        create("gitVersionPlugin") {
            id = "${project.group}.${project.name}"
            implementationClass = "net.agl.gradle.GitVersionPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.4.0.202509020913-r")
}

sourceSets {
    main {
        java {
            srcDirs("buildSrc/src/main/kotlin")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "nexus"
            url = uri(
                if (project.version.toString().endsWith("-SNAPSHOT"))
                    (findProperty("repo.publish.snapshots")
                        ?: findProperty("repo.publish.releases"))!! as String
                else findProperty("repo.publish.releases")!! as String
            )
            credentials {
                username = findProperty("repo.publish.username")!! as String
                password = findProperty("repo.publish.password")!! as String
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}
