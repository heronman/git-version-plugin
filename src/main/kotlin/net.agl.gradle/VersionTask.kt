package net.agl.gradle

import AnsiColors
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import versionFromGit

class VersionTask : DefaultTask() {
    init {
        group = "build"
        description = "Build project version from GIT tags"
    }

    @TaskAction
    fun action() {
        doLast {
            project.version = versionFromGit(project)
            println("Version calculated: ${AnsiColors.GREEN}${project.version}${AnsiColors.RESET}")
        }

    }
}
