/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.compound.screenshot

import com.github.takahirom.roborazzi.captureRoboImage
import com.zenobia.app.compound.previews.TypographyPreview
import com.zenobia.app.compound.screenshot.utils.screenshotFile
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MaterialTypographyTest : RobolectricTest() {
    @Test
    @Config(sdk = [35], qualifiers = "h2048dp-xxhdpi")
    fun screenshots() {
        captureRoboImage(file = screenshotFile("Material Typography.png")) {
            TypographyPreview()
        }
    }
}
