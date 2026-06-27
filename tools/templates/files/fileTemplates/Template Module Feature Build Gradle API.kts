plugins {
    id("com.zenobia.android-library")
}

android {
    namespace = "com.zenobia.app.features.${MODULE_NAME}.api"
}

dependencies {
    implementation(projects.libraries.architecture)
}
