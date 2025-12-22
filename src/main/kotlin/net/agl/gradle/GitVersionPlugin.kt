package net.agl.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import versionFromGit

class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val ext = object {
            val projectVersion: String by lazy { versionFromGit(project) }
            val publishingVersion: String by lazy {
                projectVersion.split("-", limit = 2)
                    .let { v -> if (v.size == 2 && v[1] != "RELEASE") "${v[0]}-SNAPSHOT" else v[0] }
            }
        }

        versionFromGit(project).also {
            project.extensions.add("versionFromGit", ext)
        }
    }
}