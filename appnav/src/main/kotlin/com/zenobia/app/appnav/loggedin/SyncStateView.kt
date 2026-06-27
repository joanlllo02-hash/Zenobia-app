/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.loggedin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zenobia.app.libraries.designsystem.components.async.AsyncIndicator
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun SyncStateView(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = fadeIn(spring(stiffness = 500F)),
        exit = fadeOut(spring(stiffness = 500F)),
    ) {
        AsyncIndicator.Loading(
            text = stringResource(id = CommonStrings.common_syncing),
        )
    }
}

@PreviewsDayNight
@Composable
internal fun SyncStateViewPreview() = ZenobiaPreview {
    // Add a box to see the shadow
    Box(modifier = Modifier.padding(24.dp)) {
        SyncStateView(
            isVisible = true
        )
    }
}
