package net.agl.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.allprojects.forEach {
            it.tasks.register("version", VersionTask::class.java)
            it.tasks
                .filter { it.name == "classes" || it.group == "publishing" }
                .forEach {
                    it.dependsOn("version")
                    it.mustRunAfter("version")
                }
        }
    }
}
