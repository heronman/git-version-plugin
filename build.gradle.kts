plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "net.agl.gradle"
version = "0.0.0-SNAPSHOT"

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

val versionFromGitTask = tasks.register("versionFromGit", DefaultTask::class.java) {
    group = "build"
    description = "Get project version from GIT tags"

    doLast {
        project.version = versionFromGit(project)
        println("Version calculated: ${AnsiColors.GREEN_BRIGHT}${project.version}${AnsiColors.RESET}")
    }
}
project.tasks.getByName("classes").dependsOn(versionFromGitTask)
project.tasks.filter { it.group == "publishing" }.forEach {
    it.dependsOn(versionFromGitTask)
    it.mustRunAfter(versionFromGitTask)
}

publishing {
    repositories {
        maven {
            name = "mavenRepo"
            url = uri(providers.provider {
                if (version.toString().endsWith("-SNAPSHOT"))
                    (findProperty("repo.publish.snapshots")
                        ?: findProperty("repo.publish.releases"))!! as String
                else
                    findProperty("repo.publish.releases")!! as String
            })
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
