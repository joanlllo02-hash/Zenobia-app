/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.matrix.ui.messages.reply.InReplyToDetails
import com.zenobia.app.libraries.matrix.ui.messages.reply.InReplyToDetailsOtherProvider

@PreviewsDayNight
@Composable
internal fun TimelineItemEventRowWithReplyOtherPreview(
    @PreviewParameter(InReplyToDetailsOtherProvider::class) inReplyToDetails: InReplyToDetails,
) = ZenobiaPreview {
    TimelineItemEventRowWithReplyContentToPreview(inReplyToDetails)
}
