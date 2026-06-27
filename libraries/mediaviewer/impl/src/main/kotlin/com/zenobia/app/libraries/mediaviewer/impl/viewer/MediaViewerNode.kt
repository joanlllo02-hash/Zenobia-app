/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.annotations.ContributesNode
import com.zenobia.app.compound.colors.SemanticColorsLightDark
import com.zenobia.app.compound.theme.ForcedDarkZenobiaTheme
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.viewfolder.api.TextFileViewer
import com.zenobia.app.libraries.architecture.callback
import com.zenobia.app.libraries.architecture.inputs
import com.zenobia.app.libraries.audio.api.AudioFocus
import com.zenobia.app.libraries.core.coroutine.CoroutineDispatchers
import com.zenobia.app.libraries.di.RoomScope
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.matrix.api.media.MatrixMediaLoader
import com.zenobia.app.libraries.matrix.api.timeline.Timeline
import com.zenobia.app.libraries.mediaviewer.api.MediaViewerEntryPoint
import com.zenobia.app.libraries.mediaviewer.api.local.LocalMediaFactory
import com.zenobia.app.libraries.mediaviewer.impl.datasource.FocusedTimelineMediaGalleryDataSourceFactory
import com.zenobia.app.libraries.mediaviewer.impl.datasource.TimelineMediaGalleryDataSource
import com.zenobia.app.libraries.mediaviewer.impl.model.hasEvent
import com.zenobia.app.services.toolbox.api.systemclock.SystemClock

@ContributesNode(RoomScope::class)
@AssistedInject
class MediaViewerNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: MediaViewerPresenter.Factory,
    timelineMediaGalleryDataSource: TimelineMediaGalleryDataSource,
    focusedTimelineMediaGalleryDataSourceFactory: FocusedTimelineMediaGalleryDataSourceFactory,
    mediaLoader: MatrixMediaLoader,
    localMediaFactory: LocalMediaFactory,
    coroutineDispatchers: CoroutineDispatchers,
    systemClock: SystemClock,
    pagerKeysHandler: PagerKeysHandler,
    private val textFileViewer: TextFileViewer,
    private val audioFocus: AudioFocus,
    private val sessionId: SessionId,
    private val enterpriseService: EnterpriseService,
) : Node(buildContext, plugins = plugins),
    MediaViewerNavigator {
    private val callback: MediaViewerEntryPoint.Callback = callback()
    private val inputs = inputs<MediaViewerEntryPoint.Params>()

    override fun onViewInTimelineClick(eventId: EventId) {
        callback.viewInTimeline(eventId)
    }

    override fun onForwardClick(eventId: EventId, fromPinnedEvents: Boolean) {
        callback.forwardEvent(eventId, fromPinnedEvents)
    }

    override fun onItemDeleted() {
        callback.onDone()
    }

    private val mediaGallerySource = if (inputs.mode == MediaViewerEntryPoint.MediaViewerMode.SingleMedia) {
        SingleMediaGalleryDataSource.createFrom(inputs)
    } else {
        val eventId = inputs.eventId
        if (eventId == null) {
            // Should not happen
            timelineMediaGalleryDataSource
        } else {
            // Can we use a specific timeline?
            val timelineMode = inputs.mode.getTimelineMode()
            when (timelineMode) {
                null -> timelineMediaGalleryDataSource
                Timeline.Mode.Live,
                is Timeline.Mode.FocusedOnEvent,
                is Timeline.Mode.Thread -> {
                    // Does timelineMediaGalleryDataSource knows the eventId?
                    val lastData = timelineMediaGalleryDataSource.getLastData().dataOrNull()
                    val isEventKnown = lastData?.hasEvent(eventId) == true
                    if (isEventKnown) {
                        timelineMediaGalleryDataSource
                    } else {
                        focusedTimelineMediaGalleryDataSourceFactory.createFor(
                            eventId = eventId,
                            mediaItem = inputs.toMediaItem(),
                            onlyPinnedEvents = false,
                        )
                    }
                }
                Timeline.Mode.PinnedEvents -> {
                    focusedTimelineMediaGalleryDataSourceFactory.createFor(
                        eventId = eventId,
                        mediaItem = inputs.toMediaItem(),
                        onlyPinnedEvents = true,
                    )
                }
                Timeline.Mode.Media -> timelineMediaGalleryDataSource
            }
        }
    }

    private val presenter = presenterFactory.create(
        inputs = inputs,
        navigator = this,
        dataSource = MediaViewerDataSource(
            mode = inputs.mode,
            coroutineScope = lifecycleScope,
            dispatcher = coroutineDispatchers.computation,
            galleryDataSource = mediaGallerySource,
            mediaLoader = mediaLoader,
            localMediaFactory = localMediaFactory,
            systemClock = systemClock,
            pagerKeysHandler = pagerKeysHandler,
        )
    )

    @Composable
    override fun View(modifier: Modifier) {
        val colors by remember {
            enterpriseService.semanticColorsFlow(sessionId = sessionId)
        }.collectAsState(SemanticColorsLightDark.default)
        ForcedDarkZenobiaTheme(
            colors = colors,
        ) {
            val state = presenter.present()
            MediaViewerView(
                state = state,
                textFileViewer = textFileViewer,
                modifier = modifier,
                audioFocus = audioFocus,
                onBackClick = callback::onDone,
            )
        }
    }
}

internal fun MediaViewerEntryPoint.MediaViewerMode.getTimelineMode(): Timeline.Mode? {
    return when (this) {
        is MediaViewerEntryPoint.MediaViewerMode.TimelineImagesAndVideos -> timelineMode
        is MediaViewerEntryPoint.MediaViewerMode.TimelineFilesAndAudios -> timelineMode
        else -> null
    }
}
