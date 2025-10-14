package net.agl.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import versionFromGit

class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("versionFromGit", DefaultTask::class.java) {
            group = "build setup"
            description = "Get project version from GIT tags"

            doLast {
                project.version = versionFromGit(project)
            }
        }

        project.tasks
            .filter { it.name == "classes" }
            .forEach { it.dependsOn("calculateVersion") }
    }
}
