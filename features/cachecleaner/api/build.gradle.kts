/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("com.zenobia.android-library")
}

android {
    namespace = "com.zenobia.app.features.cachecleaner.api"
}

dependencies {
    implementation(projects.libraries.architecture)
    implementation(libs.androidx.startup)
}
