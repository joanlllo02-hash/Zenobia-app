/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.troubleshoot.impl.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionView
import com.zenobia.app.libraries.designsystem.components.button.BackButton
import com.zenobia.app.libraries.designsystem.components.dialogs.ConfirmationDialog
import com.zenobia.app.libraries.designsystem.components.dialogs.ErrorDialog
import com.zenobia.app.libraries.designsystem.components.list.ListItemContent
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.DropdownMenu
import com.zenobia.app.libraries.designsystem.theme.components.DropdownMenuItem
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.IconButton
import com.zenobia.app.libraries.designsystem.theme.components.ListItem
import com.zenobia.app.libraries.designsystem.theme.components.Scaffold
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.theme.components.TopAppBar
import com.zenobia.app.libraries.push.api.history.PushHistoryItem
import com.zenobia.app.libraries.troubleshoot.impl.R
import com.zenobia.app.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushHistoryView(
    state: PushHistoryState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                },
                titleStr = stringResource(R.string.screen_push_history_title),
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = CompoundIcons.OverflowVertical(),
                            contentDescription = stringResource(id = CommonStrings.a11y_user_menu),
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Show only errors") },
                            trailingIcon = if (state.showOnlyErrors) {
                                {
                                    Icon(
                                        imageVector = CompoundIcons.CheckCircleSolid(),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            } else {
                                null
                            },
                            onClick = {
                                showMenu = false
                                state.eventSink(PushHistoryEvents.SetShowOnlyErrors(state.showOnlyErrors.not()))
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = CommonStrings.action_reset)) },
                            onClick = {
                                showMenu = false
                                state.eventSink(PushHistoryEvents.Reset(requiresConfirmation = true))
                            },
                        )
                    }
                }
            )
        },
    ) { padding ->
        PushHistoryContent(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            state = state,
        )
    }

    AsyncActionView(
        async = state.resetAction,
        onSuccess = {},
        confirmationDialog = {
            ConfirmationDialog(
                content = "",
                title = stringResource(CommonStrings.dialog_title_confirmation),
                submitText = stringResource(CommonStrings.action_reset),
                cancelText = stringResource(CommonStrings.action_cancel),
                onSubmitClick = { state.eventSink(PushHistoryEvents.Reset(requiresConfirmation = false)) },
                onDismiss = { state.eventSink(PushHistoryEvents.ClearDialog) },
            )
        },
        onErrorDismiss = {},
    )

    if (state.showNotSameAccountError) {
        ErrorDialog(
            content = "Please switch account first to navigate to the event.",
            onSubmit = { state.eventSink(PushHistoryEvents.ClearDialog) }
        )
    }
}

@Composable
private fun PushHistoryContent(
    state: PushHistoryState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text("Total number of received push") },
            trailingContent = ListItemContent.Text(state.pushCounter.toString()),
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = state.pushHistoryItems,
                key = {
                    it.pushDate.toString() + it.sessionId + it.roomId + it.eventId
                },
            ) { pushHistory ->
                PushHistoryItem(
                    pushHistory,
                    onClick = {
                        val sessionId = pushHistory.sessionId
                        val roomId = pushHistory.roomId
                        val eventId = pushHistory.eventId
                        if (sessionId != null && roomId != null && eventId != null) {
                            state.eventSink(PushHistoryEvents.NavigateTo(sessionId, roomId, eventId))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PushHistoryItem(
    pushHistoryItem: PushHistoryItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
    ) {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            ) {
                Text(
                    text = pushHistoryItem.formattedDate,
                    color = ZenobiaTheme.colors.textPrimary,
                )
                Text(
                    text = pushHistoryItem.providerInfo,
                    color = ZenobiaTheme.colors.textPrimary,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                    text = pushHistoryItem.sessionId?.value ?: "No sessionId",
                    color = ZenobiaTheme.colors.textPrimary,
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = pushHistoryItem.roomId?.value ?: "No roomId",
                    color = ZenobiaTheme.colors.textPrimary,
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = pushHistoryItem.eventId?.value ?: "No eventId",
                    color = ZenobiaTheme.colors.textPrimary,
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                )
                pushHistoryItem.comment?.let {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = it,
                        color = if (pushHistoryItem.hasBeenResolved) {
                            ZenobiaTheme.colors.textSecondary
                        } else {
                            ZenobiaTheme.colors.textCriticalPrimary
                        },
                        style = ZenobiaTheme.typography.fontBodyMdRegular,
                    )
                }
            }
            if (pushHistoryItem.hasBeenResolved) {
                Icon(
                    imageVector = CompoundIcons.CheckCircleSolid(),
                    modifier = Modifier.size(24.dp),
                    tint = ZenobiaTheme.colors.iconSuccessPrimary,
                    contentDescription = null,
                )
            } else {
                Icon(
                    imageVector = CompoundIcons.Error(),
                    modifier = Modifier.size(24.dp),
                    tint = ZenobiaTheme.colors.iconCriticalPrimary,
                    contentDescription = null,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun PushHistoryViewPreview(
    @PreviewParameter(PushHistoryStateProvider::class) state: PushHistoryState,
) = ZenobiaPreview {
    PushHistoryView(
        state = state,
        onBackClick = {},
    )
}
