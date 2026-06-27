/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.signedout.impl

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import com.zenobia.app.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import com.zenobia.app.libraries.designsystem.atomic.organisms.InfoListItem
import com.zenobia.app.libraries.designsystem.atomic.organisms.InfoListOrganism
import com.zenobia.app.libraries.designsystem.atomic.pages.HeaderFooterPage
import com.zenobia.app.libraries.designsystem.components.BigIcon
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SignedOutView(
    state: SignedOutState,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = { state.eventSink(SignedOutEvents.SignInAgain) })
    HeaderFooterPage(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        header = { SignedOutHeader(state) },
        content = { SignedOutContent() },
        footer = {
            SignedOutFooter(
                onSignInAgain = { state.eventSink(SignedOutEvents.SignInAgain) },
            )
        }
    )
}

@Composable
private fun SignedOutHeader(state: SignedOutState) {
    IconTitleSubtitleMolecule(
        modifier = Modifier.padding(top = 60.dp, bottom = 12.dp),
        title = stringResource(id = R.string.screen_signed_out_title),
        subTitle = stringResource(id = R.string.screen_signed_out_subtitle, state.appName),
        iconStyle = BigIcon.Style.Default(CompoundIcons.UserProfileSolid()),
    )
}

@Composable
private fun SignedOutContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = BiasAlignment(
            horizontalBias = 0f,
            verticalBias = -0.4f
        )
    ) {
        InfoListOrganism(
            items = persistentListOf(
                InfoListItem(
                    message = stringResource(id = R.string.screen_signed_out_reason_1),
                    iconVector = CompoundIcons.Lock(),
                ),
                InfoListItem(
                    message = stringResource(id = R.string.screen_signed_out_reason_2),
                    iconVector = CompoundIcons.Devices(),
                ),
                InfoListItem(
                    message = stringResource(id = R.string.screen_signed_out_reason_3),
                    iconVector = CompoundIcons.Block(),
                ),
            ),
            textStyle = ZenobiaTheme.typography.fontBodyMdMedium,
            iconTint = ZenobiaTheme.colors.iconSecondary,
        )
    }
}

@Composable
private fun SignedOutFooter(
    onSignInAgain: () -> Unit,
) {
    ButtonColumnMolecule {
        Button(
            text = stringResource(id = CommonStrings.action_sign_in_again),
            onClick = onSignInAgain,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@PreviewsDayNight
@Composable
internal fun SignedOutViewPreview(
    @PreviewParameter(SignedOutStateProvider::class) state: SignedOutState,
) = ZenobiaPreview {
    SignedOutView(
        state = state,
    )
}
