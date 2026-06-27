/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.room.joined

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.CircularProgressIndicator
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.designsystem.utils.DelayedVisibility
import com.zenobia.app.libraries.matrix.ui.room.LoadingRoomState
import com.zenobia.app.libraries.matrix.ui.room.LoadingRoomStateProvider
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun LoadingRoomNodeView(
    state: LoadingRoomState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LoadingRoomTopBar(onBackClick)
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state is LoadingRoomState.Error) {
                    Text(
                        text = stringResource(id = CommonStrings.error_unknown),
                        color = ZenobiaTheme.colors.textSecondary,
                        style = ZenobiaTheme.typography.fontBodyMdRegular,
                    )
                } else {
                    DelayedVisibility {
                        CircularProgressIndicator()
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingRoomTopBar(
    onBackClick: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        title = {
        },
    )
}

@PreviewsDayNight
@Composable
internal fun LoadingRoomNodeViewPreview(@PreviewParameter(LoadingRoomStateProvider::class) state: LoadingRoomState) = ZenobiaPreview {
    LoadingRoomNodeView(
        state = state,
        onBackClick = {}
    )
}
