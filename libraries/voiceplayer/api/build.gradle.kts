/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */
plugins {
    id("com.zenobia.android-compose-library")
}

android {
    namespace = "com.zenobia.app.libraries.voiceplayer.api"
}

dependencies {
    implementation(libs.androidx.annotationjvm)
    implementation(libs.coroutines.core)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.matrix.api)
}
