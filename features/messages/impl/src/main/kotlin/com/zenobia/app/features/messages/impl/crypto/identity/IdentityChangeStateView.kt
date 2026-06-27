/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.crypto.identity

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.appconfig.LearnMoreConfig
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.libraries.designsystem.atomic.molecules.ComposerAlertLevel
import com.zenobia.app.libraries.designsystem.atomic.molecules.ComposerAlertMolecule
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.matrix.api.encryption.identity.IdentityState
import com.zenobia.app.libraries.matrix.api.encryption.identity.isAViolation
import com.zenobia.app.libraries.matrix.ui.room.RoomMemberIdentityStateChange
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun IdentityChangeStateView(
    state: IdentityChangeState,
    onLinkClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Pick the first identity change that is a violation
    val identityChangeViolation = state.roomMemberIdentityStateChanges.firstOrNull {
        it.identityState.isAViolation()
    }
    when (identityChangeViolation?.identityState) {
        IdentityState.PinViolation -> ViolationAlert(
            identityChangeViolation = identityChangeViolation,
            onLinkClick = onLinkClick,
            textId = CommonStrings.crypto_identity_change_pin_violation_new,
            isCritical = false,
            submitTextId = CommonStrings.action_dismiss,
            onSubmitClick = { state.eventSink(IdentityChangeEvent.PinIdentity(identityChangeViolation.identityRoomMember.userId)) },
            modifier = modifier,
        )
        IdentityState.VerificationViolation -> ViolationAlert(
            identityChangeViolation = identityChangeViolation,
            onLinkClick = onLinkClick,
            textId = CommonStrings.crypto_identity_change_verification_violation_new,
            isCritical = true,
            submitTextId = CommonStrings.crypto_identity_change_withdraw_verification_action,
            onSubmitClick = { state.eventSink(IdentityChangeEvent.WithdrawVerification(identityChangeViolation.identityRoomMember.userId)) },
            modifier = modifier,
        )
        else -> Unit
    }
}

@Composable
private fun ViolationAlert(
    identityChangeViolation: RoomMemberIdentityStateChange,
    onLinkClick: (String, Boolean) -> Unit,
    @StringRes textId: Int,
    isCritical: Boolean,
    @StringRes submitTextId: Int,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ComposerAlertMolecule(
        modifier = modifier,
        avatar = identityChangeViolation.identityRoomMember.avatarData,
        content = buildAnnotatedString {
            val learnMoreStr = stringResource(CommonStrings.action_learn_more)
            val displayName = identityChangeViolation.identityRoomMember.displayNameOrDefault
            val userIdStr = stringResource(
                CommonStrings.crypto_identity_change_pin_violation_new_user_id,
                identityChangeViolation.identityRoomMember.userId,
            )
            val fullText = stringResource(textId, displayName, userIdStr, learnMoreStr)
            append(fullText)
            val userIdStartIndex = fullText.indexOf(userIdStr)
            addStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                ),
                start = userIdStartIndex,
                end = userIdStartIndex + userIdStr.length,
            )
            val learnMoreStartIndex = fullText.lastIndexOf(learnMoreStr)
            addStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    color = ZenobiaTheme.colors.textPrimary
                ),
                start = learnMoreStartIndex,
                end = learnMoreStartIndex + learnMoreStr.length,
            )
            addLink(
                url = LinkAnnotation.Url(
                    url = LearnMoreConfig.IDENTITY_CHANGE_URL,
                    linkInteractionListener = {
                        onLinkClick(LearnMoreConfig.IDENTITY_CHANGE_URL, true)
                    }
                ),
                start = learnMoreStartIndex,
                end = learnMoreStartIndex + learnMoreStr.length,
            )
        },
        submitText = stringResource(submitTextId),
        onSubmitClick = onSubmitClick,
        level = if (isCritical) ComposerAlertLevel.Critical else ComposerAlertLevel.Info,
    )
}

@PreviewsDayNight
@Composable
internal fun IdentityChangeStateViewPreview(
    @PreviewParameter(IdentityChangeStateProvider::class) state: IdentityChangeState,
) = ZenobiaPreview {
    IdentityChangeStateView(
        state = state,
        onLinkClick = { _, _ -> },
    )
}
