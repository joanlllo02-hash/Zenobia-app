/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.compound.screenshot

import com.github.takahirom.roborazzi.captureRoboImage
import com.zenobia.app.compound.screenshot.utils.screenshotFile
import com.zenobia.app.compound.theme.AvatarColorsPreviewDark
import com.zenobia.app.compound.theme.AvatarColorsPreviewLight
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AvatarColorsTest : RobolectricTest() {
    @Test
    @Config(sdk = [35], qualifiers = "xxhdpi")
    fun screenshots() {
        captureRoboImage(file = screenshotFile("Avatar Colors - Light.png")) {
            AvatarColorsPreviewLight()
        }
        captureRoboImage(file = screenshotFile("Avatar Colors - Dark.png")) {
            AvatarColorsPreviewDark()
        }
    }
}
