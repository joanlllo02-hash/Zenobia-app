/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */
plugins {
    id("com.zenobia.android-compose-library")
}

android {
    namespace = "com.zenobia.app.features.rageshake.api"
}

dependencies {
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.matrix.api)
    implementation(projects.libraries.uiStrings)
}
