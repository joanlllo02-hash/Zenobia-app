import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("com.zenobia.root")
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dependencycheck) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.dependencyanalysis)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.dependencygraph)
    alias(libs.plugins.sonarqube)
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.layout.buildDirectory)
}

private val catalog = the<LibrariesForLibs>()

allprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }
    detekt {
        buildUponDefaultConfig = true
        allRules = true
        config.from(files("$rootDir/tools/detekt/detekt.yml"))
    }
    dependencies {
        detektPlugins(catalog.detekt.compose.rules)
        detektPlugins(project(":tests:detekt-rules"))
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        exclude("com/zenobia/app/tests/konsist/failures/**")
        exclude("org/rustls/platformverifier/**")
    }

    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version = catalog.versions.ktlint.get()
        android = true
        ignoreFailures = false
        enableExperimentalRules = true
        verbose = true
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
        val generatedPath = "${layout.buildDirectory.asFile.get()}/generated/"
        filter {
            exclude { element -> element.file.path.contains(generatedPath) }
            exclude("com/zenobia/app/tests/konsist/failures/**")
            exclude("**/SafeChildrenTransitionScope.kt")
            exclude("org/rustls/platformverifier/**")
        }
    }

    apply {
        plugin("org.owasp.dependencycheck")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            allWarningsAsErrors = project.properties["allWarningsAsErrors"] == "true"
            freeCompilerArgs.add("-Xannotation-default-target=first-only")
        }
    }
}

dependencyAnalysis {
    issues {
        all {
            onUnusedDependencies {
                exclude("com.jakewharton.timber:timber")
            }
            onUnusedAnnotationProcessors {}
            onRedundantPlugins {}
            onIncorrectConfiguration {}
        }
    }
}

sonar {
    properties {
        property("sonar.projectName", "Zenobia")
        property("sonar.projectKey", "Zenobia")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.projectVersion", "1.0")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.links.homepage", "https://github.com/zenobia/app/")
        property("sonar.links.ci", "https://github.com/zenobia/app/actions")
        property("sonar.links.scm", "https://github.com/zenobia/app/")
        property("sonar.links.issue", "https://github.com/zenobia/app/issues")
        property("sonar.organization", "zenobia")
        property("sonar.login", if (project.hasProperty("SONAR_LOGIN")) project.property("SONAR_LOGIN")!! else "invalid")
        property("sonar.exclusions", "**/BugReporterMultipartBody.java")
    }
}

allprojects {
    tasks.withType<Test> {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

        val isScreenshotTest = project.gradle.startParameter.taskNames.any { it.contains("paparazzi", ignoreCase = true) }
        if (isScreenshotTest) {
            maxHeapSize = "2g"
            if (project.hasProperty("allLanguagesNoEnglish")) {
                exclude("ui/*.class")
            } else if (project.hasProperty("allLanguages").not()) {
                exclude("translations/*.class")
            }
        } else {
            exclude("ui/*.class")
            exclude("translations/*.class")
        }
    }
}

tasks.register("runQualityChecks") {
    dependsOn(":tests:konsist:testDebugUnitTest")
    dependsOn(":app:lintGplayDebug")
    project.subprojects {
        tasks.findByPath("$path:lintDebug")?.let { dependsOn(it) }
        tasks.findByName("detekt")?.let { dependsOn(it) }
        tasks.findByName("ktlintCheck")?.let { dependsOn(it) }
    }
    dependsOn("checkDocs")
    gradle.startParameter.isContinueOnFailure = true
}

tasks.register("checkDocs", Exec::class.java) {
    inputs.files("./*.md", "docs/**/*.md")
    commandLine("python3", "tools/docs/generate_toc.py", "--verify", *inputs.files.map { it.path }.toTypedArray())
}

tasks.register("generateDocsToc", Exec::class.java) {
    inputs.files("./*.md", "docs/**/*.md")
    commandLine("python3", "tools/docs/generate_toc.py", *inputs.files.map { it.path }.toTypedArray())
}

subprojects {
    val snapshotsDir = File("${project.projectDir}/src/test/snapshots")
    val removeOldScreenshotsTask = tasks.register("removeOldSnapshots") {
        onlyIf { snapshotsDir.exists() }
        doFirst {
            println("Delete previous screenshots located at $snapshotsDir\n")
            snapshotsDir.deleteRecursively()
        }
    }
    tasks.findByName("recordPaparazzi")?.dependsOn(removeOldScreenshotsTask)
    tasks.findByName("recordPaparazziDebug")?.dependsOn(removeOldScreenshotsTask)
    tasks.findByName("recordPaparazziRelease")?.dependsOn(removeOldScreenshotsTask)
}

subprojects {
    val screenshotsDir = File("${project.projectDir}/screenshots")
    val removeOldScreenshotsTask = tasks.register("removeOldScreenshots") {
        onlyIf { screenshotsDir.exists() }
        doFirst {
            println("Delete previous screenshots located at $screenshotsDir\n")
            screenshotsDir.deleteRecursively()
        }
    }
    tasks.findByName("recordRoborazzi")?.dependsOn(removeOldScreenshotsTask)
    tasks.findByName("recordRoborazziDebug")?.dependsOn(removeOldScreenshotsTask)
    tasks.findByName("recordRoborazziRelease")?.dependsOn(removeOldScreenshotsTask)
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs.addAll(
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                            "${project.layout.buildDirectory.asFile.get().absolutePath}/compose_compiler"
                    )
                )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs.addAll(
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                            "${project.layout.buildDirectory.asFile.get().absolutePath}/compose_compiler"
                    )
                )
            }
        }
    }
}
