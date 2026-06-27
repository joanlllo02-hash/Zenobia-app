/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.confirmaccountprovider

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.login.impl.R
import com.zenobia.app.features.login.impl.login.LoginModeView
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import com.zenobia.app.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import com.zenobia.app.libraries.designsystem.atomic.pages.HeaderFooterPage
import com.zenobia.app.libraries.designsystem.components.BigIcon
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.TextButton
import com.zenobia.app.libraries.matrix.api.auth.OAuthDetails
import com.zenobia.app.libraries.testtags.TestTags
import com.zenobia.app.libraries.testtags.testTag
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun ConfirmAccountProviderView(
    state: ConfirmAccountProviderState,
    onOAuthDetails: (OAuthDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit,
    onChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading by remember(state.loginMode) {
        derivedStateOf {
            state.loginMode is AsyncData.Loading
        }
    }
    val eventSink = state.eventSink

    HeaderFooterPage(
        modifier = modifier,
        header = {
            IconTitleSubtitleMolecule(
                modifier = Modifier.padding(top = 60.dp),
                iconStyle = BigIcon.Style.Default(CompoundIcons.UserProfileSolid()),
                title = stringResource(
                    id = if (state.isAccountCreation) {
                        R.string.screen_account_provider_signup_title
                    } else {
                        R.string.screen_account_provider_signin_title
                    },
                    state.accountProvider.title
                ),
                subTitle = stringResource(
                    id = if (state.isAccountCreation) {
                        R.string.screen_account_provider_signup_subtitle
                    } else {
                        R.string.screen_account_provider_signin_subtitle
                    },
                )
            )
        },
        footer = {
            ButtonColumnMolecule {
                Button(
                    text = stringResource(id = CommonStrings.action_continue),
                    showProgress = isLoading,
                    onClick = { eventSink.invoke(ConfirmAccountProviderEvents.Continue) },
                    enabled = state.submitEnabled || isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.loginContinue)
                )
                TextButton(
                    text = stringResource(id = R.string.screen_account_provider_change),
                    onClick = onChange,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.loginChangeServer)
                )
            }
        }
    ) {
        LoginModeView(
            loginMode = state.loginMode,
            onClearError = {
                eventSink(ConfirmAccountProviderEvents.ClearError)
            },
            onLearnMoreClick = onLearnMoreClick,
            onOAuthDetails = onOAuthDetails,
            onNeedLoginPassword = onNeedLoginPassword,
            onCreateAccountContinue = onCreateAccountContinue,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun ConfirmAccountProviderViewPreview(
    @PreviewParameter(ConfirmAccountProviderStateProvider::class) state: ConfirmAccountProviderState
) = ZenobiaPreview {
    ConfirmAccountProviderView(
        state = state,
        onOAuthDetails = {},
        onNeedLoginPassword = {},
        onCreateAccountContinue = {},
        onLearnMoreClick = {},
        onChange = {},
    )
}
