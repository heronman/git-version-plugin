publishing {
    publications {
        create("publishAglNexus", MavenPublication::class) {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(project.components.getByName("java"))
        }
    }

    repositories {
        maven {
            name = "repoAglNexus"
            url = project.uri(
                if (project.version.toString().endsWith("-SNAPSHOT"))
                    project.findProperty("agl.repo.url.snapshots")!! as String
                else
                    project.findProperty("agl.repo.url.releases")!! as String
            )
            credentials {
                username = project.findProperty("agl.repo.publish.username")!! as String
                password = project.findProperty("agl.repo.publish.password")!! as String
            }
        }
    }
}

project.tasks.register("publishAglNexus", DefaultTask::class) {
    group = "publishing"
    description = "Publishes to the AGL Nexus repository."
    dependsOn(
        "publishPublishAglNexusPublicationToMavenPublishRepository",
        "publishAglPublishPluginPluginMarkerMavenPublicationToMavenPublishRepository"
    )
}
