/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.features.messages.impl.timeline.TimelineEvent
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItem
import com.zenobia.app.features.messages.impl.timeline.model.event.isEdited
import com.zenobia.app.features.messages.impl.timeline.model.event.isRedacted
import com.zenobia.app.libraries.core.bool.orFalse
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LocalEventSendState
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun TimelineEventTimestampView(
    event: TimelineItem.Event,
    eventSink: (TimelineEvent.TimelineItemEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val formattedTime = event.sentTime
    val hasError = event.failedToSend
    val hasEncryptionCritical = event.messageShield?.isCritical.orFalse()
    val isMessageEdited = event.content.isEdited()
    val isMessageRedacted = event.content.isRedacted()
    val tint = if (hasError || hasEncryptionCritical && !isMessageRedacted) ZenobiaTheme.colors.textCriticalPrimary else ZenobiaTheme.colors.textSecondary

    val shield = event.messageShield
    val isVerifiedUserSendFailure = event.localSendState is LocalEventSendState.Failed.VerifiedUser
    val onClickLabel = when {
        shield != null -> stringResource(CommonStrings.a11y_view_details)
        hasError && isVerifiedUserSendFailure -> stringResource(CommonStrings.action_open_context_menu)
        else -> null
    }
    val clickableModifier = remember(shield, hasError) {
        when {
            shield != null -> {
                Modifier.clickable(
                    onClickLabel = onClickLabel,
                ) {
                    eventSink(TimelineEvent.ShowShieldDialog(shield))
                }
            }
            hasError -> Modifier
                .clickable(
                    enabled = isVerifiedUserSendFailure,
                    onClickLabel = onClickLabel,
                ) {
                    eventSink(TimelineEvent.ComputeVerifiedUserSendFailure(event))
                }
            else -> Modifier
        }
    }
    Row(
        modifier = Modifier
            .padding(PaddingValues(start = TimelineEventTimestampViewDefaults.spacing))
            // For a better click target, make the corners rounded
            .clip(RoundedCornerShape(8.dp))
            .then(clickableModifier)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isMessageEdited) {
            Text(
                stringResource(CommonStrings.common_edited_suffix),
                style = ZenobiaTheme.typography.fontBodyXsRegular,
                color = tint,
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            formattedTime,
            style = ZenobiaTheme.typography.fontBodyXsRegular,
            color = tint,
        )
        if (hasError) {
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = CompoundIcons.ErrorSolid(),
                contentDescription = stringResource(id = CommonStrings.common_sending_failed),
                tint = tint,
                modifier = Modifier.size(15.dp, 18.dp),
            )
        }

        if (!isMessageRedacted) {
            shield?.let { shield ->
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = shield.toIcon(),
                    contentDescription = stringResource(id = CommonStrings.a11y_encryption_details),
                    modifier = Modifier.size(15.dp),
                    tint = shield.toIconColor(),
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun TimelineEventTimestampViewPreview(@PreviewParameter(TimelineItemEventForTimestampViewProvider::class) event: TimelineItem.Event) = ZenobiaPreview {
    TimelineEventTimestampView(
        event = event,
        eventSink = {},
    )
}

object TimelineEventTimestampViewDefaults {
    val spacing = 16.dp
}
