/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.compound.screenshot

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.github.takahirom.roborazzi.captureRoboImage
import com.zenobia.app.compound.previews.IconsCompoundPreviewDark
import com.zenobia.app.compound.previews.IconsCompoundPreviewLight
import com.zenobia.app.compound.previews.IconsCompoundPreviewRtl
import com.zenobia.app.compound.previews.IconsPreview
import com.zenobia.app.compound.screenshot.utils.screenshotFile
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.theme.Theme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import kotlinx.collections.immutable.toImmutableList
import org.junit.Test
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CompoundIconTest : RobolectricTest() {
    @Test
    @Config(sdk = [35], qualifiers = "w1024dp-h2048dp")
    fun screenshots() {
        captureRoboImage(file = screenshotFile("Compound Icons - Light.png")) {
            IconsCompoundPreviewLight()
        }
        captureRoboImage(file = screenshotFile("Compound Icons - Rtl.png")) {
            IconsCompoundPreviewRtl()
        }
        captureRoboImage(file = screenshotFile("Compound Icons - Dark.png")) {
            IconsCompoundPreviewDark()
        }
        captureRoboImage(file = screenshotFile("Compound Vector Icons - Light.png")) {
            val content: List<@Composable ColumnScope.() -> Unit> = CompoundIcons.all.map {
                @Composable { Icon(imageVector = it, contentDescription = null) }
            }
            ZenobiaTheme {
                IconsPreview(
                    title = "Compound Vector Icons",
                    content = content.toImmutableList()
                )
            }
        }
        captureRoboImage(file = screenshotFile("Compound Vector Icons - Dark.png")) {
            val content: List<@Composable ColumnScope.() -> Unit> = CompoundIcons.all.map {
                @Composable { Icon(imageVector = it, contentDescription = null) }
            }
            ZenobiaTheme(theme = Theme.Dark) {
                IconsPreview(
                    title = "Compound Vector Icons",
                    content = content.toImmutableList()
                )
            }
        }
    }
}
