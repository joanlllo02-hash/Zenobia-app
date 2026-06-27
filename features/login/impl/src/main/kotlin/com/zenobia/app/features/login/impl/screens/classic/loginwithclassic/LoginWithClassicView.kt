/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.classic.loginwithclassic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.features.login.impl.R
import com.zenobia.app.features.login.impl.login.LoginModeView
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import com.zenobia.app.libraries.designsystem.atomic.pages.HeaderFooterPage
import com.zenobia.app.libraries.designsystem.background.OnboardingBackground
import com.zenobia.app.libraries.designsystem.components.async.AsyncActionView
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarData
import com.zenobia.app.libraries.designsystem.components.avatar.AvatarSize
import com.zenobia.app.libraries.designsystem.components.avatar.BitmapAvatar
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.OutlinedButton
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.auth.OAuthDetails
import com.zenobia.app.libraries.testtags.TestTags
import com.zenobia.app.libraries.testtags.testTag
import com.zenobia.app.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWithClassicView(
    state: LoginWithClassicState,
    onOtherOptionsClick: () -> Unit,
    onOAuthDetails: (OAuthDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading by remember(state.loginMode) {
        derivedStateOf {
            state.loginMode is AsyncData.Loading
        }
    }

    HeaderFooterPage(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        background = { OnboardingBackground() },
        isScrollable = true,
        header = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(15.dp))
                        .background(ZenobiaTheme.colors.bgCanvasDefault, shape = RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    val resId = if (state.isElementPro) {
                        R.drawable.element_pro_logo
                    } else {
                        R.drawable.element_foss_logo
                    }
                    Image(
                        modifier = Modifier.size(37.5.dp),
                        painter = painterResource(id = resId),
                        contentDescription = null,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.screen_onboarding_welcome_title),
                    color = ZenobiaTheme.colors.textPrimary,
                    style = ZenobiaTheme.typography.fontHeadingMdBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(10.dp))
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(40.dp))
                BitmapAvatar(
                    avatarData = AvatarData(
                        id = state.userId.value,
                        name = state.displayName,
                        // Not used here
                        url = null,
                        size = AvatarSize.UserHeader,
                    ),
                    bitmap = state.avatar,
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    text = stringResource(R.string.screen_onboarding_welcome_back),
                    style = ZenobiaTheme.typography.fontBodyMdRegular,
                    color = ZenobiaTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
                // User display name
                if (state.displayName != null) {
                    Text(
                        text = state.displayName,
                        style = ZenobiaTheme.typography.fontHeadingLgBold,
                        color = ZenobiaTheme.colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                }
                // UserId
                Text(
                    text = state.userId.value,
                    style = if (state.displayName == null) ZenobiaTheme.typography.fontHeadingLgBold else ZenobiaTheme.typography.fontBodyLgRegular,
                    color = ZenobiaTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                // Min spacing
                Spacer(Modifier.height(45.dp))
                ButtonColumnMolecule {
                    Button(
                        text = stringResource(CommonStrings.action_continue),
                        showProgress = isLoading,
                        onClick = {
                            state.eventSink(LoginWithClassicEvent.Submit)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(TestTags.loginContinue)
                    )
                    OutlinedButton(
                        text = stringResource(CommonStrings.common_other_options),
                        onClick = onOtherOptionsClick,
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(TestTags.loginContinue)
                    )
                }
            }
        },
        footer = {},
    )

    AsyncActionView(
        async = state.loginWithClassicAction,
        onErrorDismiss = {
            state.eventSink(LoginWithClassicEvent.ClearError)
        },
        onSuccess = {
            // noop, the view will be closed
        },
        progressDialog = {
            // The button is showing the progress
        }
    )
    LoginModeView(
        loginMode = state.loginMode,
        onClearError = {
            state.eventSink(LoginWithClassicEvent.ClearError)
        },
        onLearnMoreClick = onLearnMoreClick,
        onOAuthDetails = onOAuthDetails,
        onNeedLoginPassword = onNeedLoginPassword,
        onCreateAccountContinue = onCreateAccountContinue,
    )
}

@PreviewsDayNight
@Composable
internal fun LoginWithClassicViewPreview(@PreviewParameter(LoginWithClassicStateProvider::class) state: LoginWithClassicState) = ZenobiaPreview {
    LoginWithClassicView(
        state = state,
        onOtherOptionsClick = {},
        onOAuthDetails = {},
        onNeedLoginPassword = {},
        onLearnMoreClick = {},
        onCreateAccountContinue = {},
    )
}
