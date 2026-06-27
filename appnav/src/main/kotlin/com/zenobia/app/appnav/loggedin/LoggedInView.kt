/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.loggedin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.Lifecycle
import com.zenobia.app.appnav.R
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.components.dialogs.ErrorDialog
import com.zenobia.app.libraries.designsystem.components.dialogs.ErrorDialogWithDoNotShowAgain
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.utils.OnLifecycleEvent
import com.zenobia.app.libraries.matrix.api.exception.isNetworkError
import com.zenobia.app.libraries.push.api.PusherRegistrationFailure
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun LoggedInView(
    state: LoggedInState,
    navigateToNotificationTroubleshoot: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnLifecycleEvent { _, event ->
         if (event == Lifecycle.Event.ON_RESUME) {
            state.eventSink(LoggedInEvents.CheckSlidingSyncProxyAvailability)
         }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        SyncStateView(
            modifier = Modifier.align(Alignment.TopCenter),
            isVisible = state.showSyncSpinner,
        )
    }
    when (state.pusherRegistrationState) {
        is AsyncData.Uninitialized,
        is AsyncData.Loading,
        is AsyncData.Success -> Unit
        is AsyncData.Failure -> {
            state.pusherRegistrationState.errorOrNull()
                ?.takeIf { !state.ignoreRegistrationError }
                ?.getReason()
                ?.let { reason ->
                    ErrorDialogWithDoNotShowAgain(
                        content = stringResource(id = CommonStrings.common_error_registering_pusher_android, reason),
                        cancelText = stringResource(id = CommonStrings.common_settings),
                        onDismiss = {
                            state.eventSink(LoggedInEvents.CloseErrorDialog(it))
                        },
                        onCancel = {
                            state.eventSink(LoggedInEvents.CloseErrorDialog(false))
                            navigateToNotificationTroubleshoot()
                        }
                    )
                }
        }
    }

    // Set the force migration dialog here so it's always displayed over every screen
    if (state.forceNativeSlidingSyncMigration) {
        ForceNativeSlidingSyncMigrationDialog(
            appName = state.appName,
            onSubmit = {
                state.eventSink(LoggedInEvents.LogoutAndMigrateToNativeSlidingSync)
            }
        )
    }
}

private fun Throwable.getReason(): String? {
    return when (this) {
        is PusherRegistrationFailure.RegistrationFailure -> {
            if (isRegisteringAgain && clientException.isNetworkError()) {
                // When registering again, ignore network error
                null
            } else {
                clientException.message ?: "Unknown error"
            }
        }
        is PusherRegistrationFailure.AccountNotVerified -> null
        is PusherRegistrationFailure.NoDistributorsAvailable -> "No distributors available"
        is PusherRegistrationFailure.NoProvidersAvailable -> "No providers available"
        else -> "Other error: $message"
    }
}

@Composable
private fun ForceNativeSlidingSyncMigrationDialog(
    appName: String,
    onSubmit: () -> Unit,
) {
    ErrorDialog(
        title = null,
        content = stringResource(R.string.banner_migrate_to_native_sliding_sync_app_force_logout_title, appName),
        submitText = stringResource(R.string.banner_migrate_to_native_sliding_sync_action),
        onSubmit = onSubmit,
        canDismiss = false,
    )
}

@PreviewsDayNight
@Composable
internal fun LoggedInViewPreview(@PreviewParameter(LoggedInStateProvider::class) state: LoggedInState) = ZenobiaPreview {
    LoggedInView(
        state = state,
        navigateToNotificationTroubleshoot = {},
    )
}
