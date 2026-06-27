plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.appdistribution.gradle)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.autonomousapps.dependencyanalysis.plugin)
    implementation(libs.metro.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.compose.compiler.plugin)
}
