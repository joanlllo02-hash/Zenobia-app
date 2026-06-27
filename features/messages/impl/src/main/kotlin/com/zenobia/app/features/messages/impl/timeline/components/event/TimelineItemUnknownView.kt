/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zenobia.app.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import com.zenobia.app.libraries.designsystem.icons.CompoundDrawables
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun TimelineItemUnknownView(
    @Suppress("UNUSED_PARAMETER") content: TimelineItemUnknownContent,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit,
    modifier: Modifier = Modifier
) {
    TimelineItemInformativeView(
        text = stringResource(id = CommonStrings.common_unsupported_event),
        iconDescription = stringResource(id = CommonStrings.dialog_title_warning),
        iconResourceId = CompoundDrawables.ic_compound_info_solid,
        onContentLayoutChange = onContentLayoutChange,
        modifier = modifier
    )
}

@PreviewsDayNight
@Composable
internal fun TimelineItemUnknownViewPreview() = ZenobiaPreview {
    TimelineItemUnknownView(
        content = TimelineItemUnknownContent,
        onContentLayoutChange = {},
    )
}
