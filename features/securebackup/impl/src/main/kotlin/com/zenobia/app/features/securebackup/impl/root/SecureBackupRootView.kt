/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.securebackup.impl.root

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.features.securebackup.impl.R
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionView
import com.zenobia.app.libraries.designsystem.components.dialogs.ErrorDialog
import com.zenobia.app.libraries.designsystem.components.list.ListItemContent
import com.zenobia.app.libraries.designsystem.components.preferences.PreferencePage
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.text.buildAnnotatedStringWithStyledPart
import com.zenobia.app.libraries.designsystem.theme.components.CircularProgressIndicator
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.ListItem
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarHost
import com.zenobia.app.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import com.zenobia.app.libraries.matrix.api.encryption.BackupState
import com.zenobia.app.libraries.matrix.api.encryption.RecoveryState
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun SecureBackupRootView(
    state: SecureBackupRootState,
    onBackClick: () -> Unit,
    onSetupClick: () -> Unit,
    onChangeClick: () -> Unit,
    onDisableClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onLearnMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)

    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = stringResource(id = CommonStrings.common_encryption),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.screen_chat_backup_key_backup_title),
                )
            },
            supportingContent = {
                Text(
                    text = buildAnnotatedStringWithStyledPart(
                        fullTextRes = R.string.screen_chat_backup_key_backup_description,
                        coloredTextRes = CommonStrings.action_learn_more,
                        color = ZenobiaTheme.colors.textPrimary,
                        underline = false,
                        bold = true,
                    ),
                )
            },
            onClick = onLearnMoreClick,
        )

        // Disable / Enable key storage
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.screen_chat_backup_key_storage_toggle_title),
                )
            },
            trailingContent = when (state.backupState) {
                BackupState.WAITING_FOR_SYNC,
                BackupState.DISABLING -> ListItemContent.Custom { LoadingView() }
                BackupState.UNKNOWN -> {
                    when (state.doesBackupExistOnServer) {
                        is AsyncData.Success -> {
                            ListItemContent.Switch(checked = state.doesBackupExistOnServer.data)
                        }
                        is AsyncData.Loading,
                        AsyncData.Uninitialized -> ListItemContent.Custom { LoadingView() }
                        is AsyncData.Failure -> ListItemContent.Custom {
                            Text(
                                text = stringResource(id = CommonStrings.action_retry)
                            )
                        }
                    }
                }
                BackupState.CREATING,
                BackupState.ENABLING,
                BackupState.RESUMING,
                BackupState.ENABLED,
                BackupState.DOWNLOADING -> ListItemContent.Switch(checked = true)
            },
            onClick = {
                when (state.backupState) {
                    BackupState.WAITING_FOR_SYNC,
                    BackupState.DISABLING -> Unit
                    BackupState.UNKNOWN -> {
                        when (state.doesBackupExistOnServer) {
                            is AsyncData.Success -> {
                                if (state.doesBackupExistOnServer.data) {
                                    onDisableClick()
                                } else {
                                    state.eventSink.invoke(SecureBackupRootEvents.EnableKeyStorage)
                                }
                            }
                            is AsyncData.Loading,
                            AsyncData.Uninitialized -> Unit
                            is AsyncData.Failure -> state.eventSink.invoke(SecureBackupRootEvents.RetryKeyBackupState)
                        }
                    }
                    BackupState.CREATING,
                    BackupState.ENABLING,
                    BackupState.RESUMING,
                    BackupState.ENABLED,
                    BackupState.DOWNLOADING -> onDisableClick()
                }
            },
        )
        HorizontalDivider()
        // Setup recovery
        when (state.recoveryState) {
            RecoveryState.UNKNOWN,
            RecoveryState.WAITING_FOR_SYNC -> Unit
            RecoveryState.DISABLED -> {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.screen_chat_backup_recovery_action_setup),
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(id = R.string.screen_chat_backup_recovery_action_setup_description, state.appName),
                        )
                    },
                    trailingContent = ListItemContent.Badge,
                    enabled = state.isKeyStorageEnabled,
                    alwaysClickable = true,
                    onClick = {
                        if (state.isKeyStorageEnabled) {
                            onSetupClick()
                        } else {
                            state.eventSink.invoke(SecureBackupRootEvents.DisplayKeyStorageDisabledError)
                        }
                    },
                )
            }
            RecoveryState.ENABLED -> {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.screen_chat_backup_recovery_action_change),
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(id = R.string.screen_chat_backup_recovery_action_change_description),
                        )
                    },
                    enabled = state.isKeyStorageEnabled,
                    alwaysClickable = true,
                    onClick = {
                        if (state.isKeyStorageEnabled) {
                            onChangeClick()
                        } else {
                            state.eventSink.invoke(SecureBackupRootEvents.DisplayKeyStorageDisabledError)
                        }
                    },
                )
            }
            RecoveryState.INCOMPLETE ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.screen_chat_backup_recovery_action_confirm),
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(id = R.string.screen_chat_backup_recovery_action_confirm_description),
                        )
                    },
                    trailingContent = ListItemContent.Badge,
                    enabled = state.isKeyStorageEnabled,
                    alwaysClickable = true,
                    onClick = {
                        if (state.isKeyStorageEnabled) {
                            onConfirmRecoveryKeyClick()
                        } else {
                            state.eventSink.invoke(SecureBackupRootEvents.DisplayKeyStorageDisabledError)
                        }
                    },
                )
        }
    }

    AsyncActionView(
        async = state.enableAction,
        progressDialog = { },
        onSuccess = { },
        onErrorDismiss = { state.eventSink.invoke(SecureBackupRootEvents.DismissDialog) }
    )
    if (state.displayKeyStorageDisabledError) {
        ErrorDialog(
            title = null,
            content = stringResource(id = R.string.screen_chat_backup_key_storage_disabled_error),
            onSubmit = { state.eventSink.invoke(SecureBackupRootEvents.DismissDialog) },
        )
    }
}

@Composable
private fun LoadingView() {
    CircularProgressIndicator(
        modifier = Modifier
            .progressSemantics()
            .size(24.dp),
        strokeWidth = 2.dp
    )
}

@PreviewsDayNight
@Composable
internal fun SecureBackupRootViewPreview(
    @PreviewParameter(SecureBackupRootStateProvider::class) state: SecureBackupRootState
) = ZenobiaPreview {
    SecureBackupRootView(
        state = state,
        onBackClick = {},
        onSetupClick = {},
        onChangeClick = {},
        onDisableClick = {},
        onConfirmRecoveryKeyClick = {},
        onLearnMoreClick = {},
    )
}
