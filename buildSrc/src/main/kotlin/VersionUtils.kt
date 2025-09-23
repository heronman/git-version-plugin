import org.gradle.api.Project
import java.io.File
import java.security.MessageDigest

object VersionUtils {
    private val red = "\u001B[31m"
    private val yellow = "\u001B[33m"
    private val reset = "\u001B[0m"

    // helper: run command and return stdout
    private fun Project.execAndGetStdout(command: List<String>): String {
        providers.exec {
            commandLine(command)
            isIgnoreExitValue = true
        }.let {
            val err = it.standardError.asText.get().trim()
            if (err.isNotBlank()) {
                System.err.println("${yellow}WARNING: Error executing command [${command.joinToString(" ")}]:\n$red$err$reset\n---")
            }
            return it.standardOutput.asText.get().trim()
        }
    }

    // Helper: compute SHA256 of a file
    private fun File.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(readBytes())
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    // Helper: compute SHA256 of a string
    private fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(this.toByteArray())
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    fun computeVersion(project: Project, ): String {
        // 1. ".version" file
        File(project.rootDir, ".version")
            .takeIf { it.exists() }
            ?.readLines(Charsets.UTF_8)
            ?.map(String::trim)
            ?.firstOrNull { it.isNotEmpty() && !it.startsWith("#") }
            ?.let { return it }

        // 2. ENV variable
        System.getenv("VERSION")?.takeIf { it.isNotBlank() }?.let { return it }

        // 3. Get version from GIT
        return runCatching {
            val lastTag = project.execAndGetStdout(
                listOf("git", "describe", "--tags", "--abbrev=0", "--first-parent")
            ).ifBlank { null }

            val commitsAfterTag = lastTag?.let {
                project.execAndGetStdout(
                    listOf("git", "rev-list", "$it..HEAD", "--count", "--first-parent")
                ).toInt()
            } ?: 0

            var version = lastTag?.let {
                if (commitsAfterTag > 0) {
                    "$it-$commitsAfterTag"
                } else it
            } ?: "0.0.0"

            val dirty = project.execAndGetStdout(listOf("git", "status", "-s"))
            if (dirty.isNotBlank()) {
                val untrackedFiles =
                    project.execAndGetStdout(listOf("git", "ls-files", "--others", "--exclude-standard"))
                        .lines().filter { it.isNotBlank() }
                val untrackedHash = untrackedFiles.joinToString(" ") { file ->
                    File(project.rootDir, file).sha256()
                }
                val verboseDiff = project.execAndGetStdout(listOf("git", "status", "-vv"))
                val hashInput = if (untrackedHash.isNotBlank()) "$untrackedHash $verboseDiff" else verboseDiff
                val hash = hashInput.sha256().substring(0, 8)
                version += "-DIRTY-$hash"
            } else if (lastTag == null) {
                version += "-SNAPSHOT"
            }

            version
        }.getOrElse { "0.0.0-SNAPSHOT" }
    }
}