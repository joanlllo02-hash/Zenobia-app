/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.zenobia.app.compound.theme.ZenobiaTheme

val bgCanvasWithTransparency: Color
    @Composable
    get() = ZenobiaTheme.colors.bgCanvasDefault.copy(alpha = 0.6f)
