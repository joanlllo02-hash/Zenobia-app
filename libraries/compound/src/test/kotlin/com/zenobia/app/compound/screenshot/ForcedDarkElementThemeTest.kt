/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.compound.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.zenobia.app.compound.colors.SemanticColorsLightDark
import com.zenobia.app.compound.screenshot.utils.screenshotFile
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.theme.ForcedDarkZenobiaTheme
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import org.junit.Test
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ForcedDarkZenobiaThemeTest : RobolectricTest() {
    @Test
    @Config(sdk = [35], qualifiers = "xxhdpi")
    fun screenshots() {
        captureRoboImage(file = screenshotFile("ForcedDarkZenobiaTheme.png")) {
            ZenobiaTheme {
                Surface {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "Outside")
                        ForcedDarkZenobiaTheme(
                            colors = SemanticColorsLightDark.default,
                        ) {
                            Surface {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Text(text = "Inside ForcedDarkZenobiaTheme", modifier = Modifier.align(Alignment.Center))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
