/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.viewfolder.impl.file

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.components.async.AsyncFailure
import com.zenobia.app.libraries.designsystem.components.async.AsyncLoading
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconButton
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewFileView(
    state: ViewFileState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
                titleStr = state.name,
                actions = {
                    IconButton(
                        onClick = {
                            state.eventSink(ViewFileEvents.Share)
                        },
                    ) {
                        Icon(
                            imageVector = CompoundIcons.ShareAndroid(),
                            contentDescription = stringResource(id = CommonStrings.action_share),
                        )
                    }
                    IconButton(
                        onClick = {
                            state.eventSink(ViewFileEvents.SaveOnDisk)
                        },
                    ) {
                        Icon(
                            imageVector = CompoundIcons.Download(),
                            contentDescription = stringResource(id = CommonStrings.action_save),
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {
                when (state.lines) {
                    AsyncData.Uninitialized,
                    is AsyncData.Loading -> AsyncLoading()
                    is AsyncData.Success -> FileContent(
                        modifier = Modifier.weight(1f),
                        lines = state.lines.data.toImmutableList(),
                        colorationMode = state.colorationMode,
                    )
                    is AsyncData.Failure -> AsyncFailure(throwable = state.lines.error, onRetry = null)
                }
            }
        }
    )
}

@PreviewsDayNight
@Composable
internal fun ViewFileViewPreview(@PreviewParameter(ViewFileStateProvider::class) state: ViewFileState) = ZenobiaPreview {
    ViewFileView(
        state = state,
        onBackClick = {},
    )
}
