/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("com.zenobia.android-compose-library")
}

android {
    namespace = "com.zenobia.app.features.linknewdevice.test"
}

dependencies {
    implementation(projects.features.linknewdevice.api)
    implementation(projects.tests.testutils)
}
