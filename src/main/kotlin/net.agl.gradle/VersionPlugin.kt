package net.agl.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.version = VersionUtils.computeVersion(project)
    }
}
