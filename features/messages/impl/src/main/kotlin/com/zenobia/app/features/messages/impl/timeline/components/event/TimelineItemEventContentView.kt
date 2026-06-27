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
import com.zenobia.app.features.messages.impl.timeline.TimelineEvent
import com.zenobia.app.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import com.zenobia.app.features.messages.impl.timeline.di.LocalTimelineItemPresenterFactories
import com.zenobia.app.features.messages.impl.timeline.di.rememberPresenter
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemEventContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemFileContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemImageContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemPollContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemStateContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import com.zenobia.app.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import com.zenobia.app.features.messages.impl.timeline.model.event.ensureActiveLiveLocation
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.voiceplayer.api.VoiceMessageState
import com.zenobia.app.wysiwyg.link.Link

@Composable
fun TimelineItemEventContentView(
    content: TimelineItemEventContent,
    hideMediaContent: Boolean,
    onContentClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onShowContentClick: () -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    eventSink: (TimelineEvent.TimelineItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit = {},
) {
    val presenterFactories = LocalTimelineItemPresenterFactories.current
    when (content) {
        is TimelineItemEncryptedContent -> TimelineItemEncryptedView(
            content = content,
            onContentLayoutChange = onContentLayoutChange,
            modifier = modifier
        )
        is TimelineItemRedactedContent -> TimelineItemRedactedView(
            content = content,
            onContentLayoutChange = onContentLayoutChange,
            modifier = modifier
        )
        is TimelineItemTextBasedContent -> TimelineItemTextView(
            content = content,
            modifier = modifier,
            onLinkClick = onLinkClick,
            onLinkLongClick = onLinkLongClick,
            onContentLayoutChange = onContentLayoutChange
        )
        is TimelineItemUnknownContent -> TimelineItemUnknownView(
            content = content,
            onContentLayoutChange = onContentLayoutChange,
            modifier = modifier
        )
        is TimelineItemLocationContent -> {
            TimelineItemLocationView(
                content = content.ensureActiveLiveLocation(),
                onStopLiveLocationClick = { eventSink(TimelineEvent.StopLiveLocationShare) },
                modifier = modifier
            )
        }
        is TimelineItemImageContent -> TimelineItemImageView(
            content = content,
            hideMediaContent = hideMediaContent,
            onContentClick = onContentClick,
            onLongClick = onLongClick,
            onShowContentClick = onShowContentClick,
            onLinkClick = onLinkClick,
            onLinkLongClick = onLinkLongClick,
            onContentLayoutChange = onContentLayoutChange,
            modifier = modifier,
        )
        is TimelineItemStickerContent -> TimelineItemStickerView(
            content = content,
            hideMediaContent = hideMediaContent,
            onContentClick = onContentClick,
            onLongClick = onLongClick,
            onShowClick = onShowContentClick,
            modifier = modifier,
        )
        is TimelineItemVideoContent -> TimelineItemVideoView(
            content = content,
            hideMediaContent = hideMediaContent,
            onContentClick = onContentClick,
            onLongClick = onLongClick,
            onShowContentClick = onShowContentClick,
            onLinkClick = onLinkClick,
            onLinkLongClick = onLinkLongClick,
            onContentLayoutChange = onContentLayoutChange,
            modifier = modifier
        )
        is TimelineItemFileContent -> TimelineItemFileView(
            content = content,
            onContentLayoutChange = onContentLayoutChange,
            modifier = modifier
        )
        is TimelineItemAudioContent -> TimelineItemAudioView(
            content = content,
            onContentLayoutChange = onContentLayoutChange,
            modifier = modifier
        )
        is TimelineItemLegacyCallInviteContent -> TimelineItemLegacyCallInviteView(modifier = modifier)
        is TimelineItemStateContent -> TimelineItemStateView(
            content = content,
            modifier = modifier
        )
        is TimelineItemPollContent -> TimelineItemPollView(
            content = content,
            eventSink = eventSink,
            modifier = modifier,
        )
        is TimelineItemVoiceContent -> {
            val presenter: Presenter<VoiceMessageState> = presenterFactories.rememberPresenter(content)
            TimelineItemVoiceView(
                state = presenter.present(),
                content = content,
                onContentLayoutChange = onContentLayoutChange,
                modifier = modifier
            )
        }
        is TimelineItemRtcNotificationContent -> error("This shouldn't be rendered as the content of a bubble")
    }
}
