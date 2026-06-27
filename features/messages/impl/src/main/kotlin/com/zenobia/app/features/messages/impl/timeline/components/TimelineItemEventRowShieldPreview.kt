/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemEvent
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemGroupPosition
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemImageContent
import com.zenobia.app.features.messages.impl.timeline.model.event.aTimelineItemTextContent
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.matrix.api.timeline.item.event.MessageShield

@PreviewsDayNight
@Composable
internal fun TimelineItemEventRowShieldPreview() = ZenobiaPreview {
    Column {
        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                senderDisplayName = "Sender with a super long name that should ellipsize",
                isMine = true,
                content = aTimelineItemTextContent(
                    body = "Message sent from unsigned device"
                ),
                groupPosition = TimelineItemGroupPosition.First,
                messageShield = aCriticalShield()
            ),
        )
        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                senderDisplayName = "Sender with a super long name that should ellipsize",
                content = aTimelineItemTextContent(
                    body = "Short Message with authenticity warning"
                ),
                groupPosition = TimelineItemGroupPosition.Middle,
                messageShield = aWarningShield()
            ),
        )
        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                isMine = true,
                content = aTimelineItemImageContent(
                    aspectRatio = 2.5f
                ),
                groupPosition = TimelineItemGroupPosition.Last,
                messageShield = aCriticalShield()
            ),
        )
        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                content = aTimelineItemImageContent(
                    aspectRatio = 2.5f
                ),
                groupPosition = TimelineItemGroupPosition.Last,
                messageShield = aWarningShield()
            ),
        )
    }
}

private fun aWarningShield() = MessageShield.AuthenticityNotGuaranteed(isCritical = false)

internal fun aCriticalShield() = MessageShield.UnverifiedIdentity(isCritical = true)
