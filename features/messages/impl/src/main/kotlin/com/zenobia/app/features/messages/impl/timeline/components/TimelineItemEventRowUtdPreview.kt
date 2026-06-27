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
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemReactions
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemGroupPosition
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_ALICE
import com.zenobia.app.libraries.designsystem.preview.USER_NAME_BOB
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UnableToDecryptContent
import com.zenobia.app.libraries.matrix.api.timeline.item.event.UtdCause

@PreviewsDayNight
@Composable
internal fun TimelineItemEventRowUtdPreview() = ZenobiaPreview {
    Column {
        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                senderDisplayName = USER_NAME_ALICE,
                isMine = false,
                content = TimelineItemEncryptedContent(
                    data = UnableToDecryptContent.Data.MegolmV1AesSha2(
                        sessionId = "sessionId",
                        utdCause = UtdCause.UnsignedDevice,
                    )
                ),
                timelineItemReactions = aTimelineItemReactions(count = 0),
                groupPosition = TimelineItemGroupPosition.First,
            ),
        )
        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                senderDisplayName = USER_NAME_BOB,
                isMine = false,
                content = TimelineItemEncryptedContent(
                    data = UnableToDecryptContent.Data.MegolmV1AesSha2(
                        sessionId = "sessionId",
                        utdCause = UtdCause.VerificationViolation,
                    )
                ),
                groupPosition = TimelineItemGroupPosition.First,
                timelineItemReactions = aTimelineItemReactions(count = 0)
            ),
        )

        ATimelineItemEventRow(
            event = aTimelineItemEvent(
                senderDisplayName = USER_NAME_BOB,
                isMine = false,
                content = TimelineItemEncryptedContent(
                    data = UnableToDecryptContent.Data.MegolmV1AesSha2(
                        sessionId = "sessionId",
                        utdCause = UtdCause.SentBeforeWeJoined,
                    )
                ),
                groupPosition = TimelineItemGroupPosition.Last,
                timelineItemReactions = aTimelineItemReactions(count = 0)
            ),
        )
    }
}
