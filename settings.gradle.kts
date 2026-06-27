pluginManagement {
    repositories {
        includeBuild("plugins")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://www.jitpack.io")
            content {
                includeModule("com.github.matrix-org", "matrix-analytics-events")
                includeModule("com.github.philburk", "jsyn")
            }
        }
        google()
        mavenCentral()
        maven {
            url = uri("https://repo1.maven.org/maven2/")
        }
        flatDir {
            dirs("libraries/matrix/libs")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Zenobia"
include(":app")
include(":appnav")
include(":appconfig")
include(":appicon:element")
include(":appicon:enterprise")
include(":tests:detekt-rules")
include(":tests:konsist")
include(":tests:uitests")
include(":tests:testutils")
include(":annotations")
include(":codegen")

fun includeProjects(directory: File, path: String, maxDepth: Int = 1) {
    directory.listFiles().orEmpty().also { it.sort() }.forEach { file ->
        if (file.isDirectory) {
            val newPath = "$path:${file.name}"
            val buildFile = File(file, "build.gradle.kts")
            if (buildFile.exists()) {
                include(newPath)
                logger.lifecycle("Included project: $newPath")
            } else if (maxDepth > 0) {
                includeProjects(file, newPath, maxDepth - 1)
            }
        }
    }
}

includeProjects(File(rootDir, "enterprise"), ":enterprise", maxDepth = 2)
includeProjects(File(rootDir, "features"), ":features")
includeProjects(File(rootDir, "libraries"), ":libraries")
includeProjects(File(rootDir, "services"), ":services")
