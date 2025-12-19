package net.agl.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import versionFromGit

data class VersionFromGit(val projectVersion: String, val publishingVersion: String)

class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        versionFromGit(project).also {
            project.version = it
            project.extensions.add(
                "versionFromGit",
                VersionFromGit(
                    it,
                    it.split("-", limit = 2)
                        .let { v -> if (v.size == 2 && v[1] != "RELEASE") "${v[0]}-SNAPSHOT" else v[0] }
                )
            )
        }
    }
}