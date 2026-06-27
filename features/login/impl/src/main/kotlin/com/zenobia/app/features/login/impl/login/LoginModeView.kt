/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.features.login.impl.R
import com.zenobia.app.features.login.impl.dialogs.SlidingSyncNotSupportedDialog
import com.zenobia.app.features.login.impl.error.ChangeServerError
import com.zenobia.app.features.login.impl.screens.createaccount.AccountCreationNotSupported
import com.zenobia.app.libraries.androidutils.system.openGooglePlay
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.components.dialogs.ConfirmationDialog
import com.zenobia.app.libraries.designsystem.components.dialogs.ErrorDialog
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.LocalBuildMeta
import com.zenobia.app.libraries.matrix.api.auth.AuthenticationException
import com.zenobia.app.libraries.matrix.api.auth.OAuthDetails
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun LoginModeView(
    loginMode: AsyncData<LoginMode>,
    onClearError: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onOAuthDetails: (OAuthDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit
) {
    val context = LocalContext.current
    when (loginMode) {
        is AsyncData.Failure -> {
            when (val error = loginMode.error) {
                is ChangeServerError -> {
                    when (error) {
                        ChangeServerError.InvalidServer ->
                            ErrorDialog(
                                content = stringResource(R.string.screen_change_server_error_invalid_homeserver),
                                onSubmit = onClearError,
                            )
                        is ChangeServerError.UnsupportedServer -> {
                            ErrorDialog(
                                content = stringResource(R.string.screen_login_error_unsupported_authentication),
                                onSubmit = onClearError,
                            )
                        }
                        is ChangeServerError.Error -> {
                            ErrorDialog(
                                content = error.messageStr ?: stringResource(CommonStrings.error_unknown),
                                onSubmit = onClearError,
                            )
                        }
                        is ChangeServerError.SlidingSyncAlert -> {
                            SlidingSyncNotSupportedDialog(
                                onLearnMoreClick = {
                                    onLearnMoreClick()
                                    onClearError()
                                },
                                onDismiss = onClearError,
                            )
                        }
                        is ChangeServerError.NeedElementPro -> {
                            ConfirmationDialog(
                                title = stringResource(R.string.screen_change_server_error_element_pro_required_title),
                                content = stringResource(
                                    R.string.screen_change_server_error_element_pro_required_message,
                                    error.unauthorisedAccountProviderTitle,
                                ),
                                submitText = stringResource(R.string.screen_change_server_error_element_pro_required_action_android),
                                onSubmitClick = {
                                    context.openGooglePlay(error.applicationId)
                                    onClearError()
                                },
                                onDismiss = onClearError,
                            )
                        }
                        is ChangeServerError.UnauthorizedAccountProvider -> {
                            ErrorDialog(
                                content = stringResource(
                                    id = R.string.screen_change_server_error_unauthorized_homeserver,
                                    LocalBuildMeta.current.applicationName,
                                    error.unauthorisedAccountProviderTitle,
                                ),
                                onSubmit = onClearError,
                            )
                        }
                    }
                }
                is AccountCreationNotSupported -> {
                    ErrorDialog(
                        content = stringResource(CommonStrings.error_account_creation_not_possible),
                        onSubmit = onClearError,
                    )
                }
                is AuthenticationException.AccountAlreadyLoggedIn -> {
                    ErrorDialog(
                        content = stringResource(CommonStrings.error_account_already_logged_in, error.userId),
                        onSubmit = onClearError,
                    )
                }
                else -> {
                    ErrorDialog(
                        content = stringResource(CommonStrings.error_unknown),
                        onSubmit = onClearError,
                    )
                }
            }
        }
        is AsyncData.Loading -> Unit // The Continue button shows the loading state
        is AsyncData.Success -> {
            when (val loginModeData = loginMode.data) {
                is LoginMode.OAuth -> onOAuthDetails(loginModeData.oAuthDetails)
                LoginMode.PasswordLogin -> onNeedLoginPassword()
                is LoginMode.AccountCreation -> onCreateAccountContinue(loginModeData.url)
            }
            // Also clear the data, to let the next screen be able to go back
            onClearError()
        }
        AsyncData.Uninitialized -> Unit
    }
}

@PreviewsDayNight
@Composable
internal fun LoginModeViewPreview(@PreviewParameter(LoginModeViewErrorProvider::class) error: Throwable) {
    ZenobiaPreview {
        LoginModeView(
            loginMode = AsyncData.Failure(error),
            onClearError = {},
            onLearnMoreClick = {},
            onOAuthDetails = {},
            onNeedLoginPassword = {},
            onCreateAccountContinue = {}
        )
    }
}
