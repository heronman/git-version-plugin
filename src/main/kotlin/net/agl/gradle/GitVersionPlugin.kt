package net.agl.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.version = versionFromGit(project)
    }
}