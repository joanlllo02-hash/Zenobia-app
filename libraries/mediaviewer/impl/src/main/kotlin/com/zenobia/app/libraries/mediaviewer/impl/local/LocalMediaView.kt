/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.local

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zenobia.app.features.viewfolder.api.TextFileViewer
import com.zenobia.app.libraries.audio.api.AudioFocus
import com.zenobia.app.libraries.core.mimetype.MimeTypes
import com.zenobia.app.libraries.core.mimetype.MimeTypes.isMimeTypeAudio
import com.zenobia.app.libraries.core.mimetype.MimeTypes.isMimeTypeImage
import com.zenobia.app.libraries.core.mimetype.MimeTypes.isMimeTypeVideo
import com.zenobia.app.libraries.mediaviewer.api.MediaInfo
import com.zenobia.app.libraries.mediaviewer.api.local.LocalMedia
import com.zenobia.app.libraries.mediaviewer.impl.local.audio.MediaAudioView
import com.zenobia.app.libraries.mediaviewer.impl.local.file.MediaFileView
import com.zenobia.app.libraries.mediaviewer.impl.local.image.MediaImageView
import com.zenobia.app.libraries.mediaviewer.impl.local.pdf.MediaPdfView
import com.zenobia.app.libraries.mediaviewer.impl.local.txt.TextFileView
import com.zenobia.app.libraries.mediaviewer.impl.local.video.MediaVideoView

@Composable
fun LocalMediaView(
    localMedia: LocalMedia?,
    bottomPaddingInPixels: Int,
    audioFocus: AudioFocus?,
    onClick: () -> Unit,
    onOpenWith: (() -> Unit)?,
    textFileViewer: TextFileViewer,
    modifier: Modifier = Modifier,
    isDisplayed: Boolean = true,
    isUserSelected: Boolean = false,
    localMediaViewState: LocalMediaViewState = rememberLocalMediaViewState(),
    mediaInfo: MediaInfo? = localMedia?.info,
) {
    val mimeType = mediaInfo?.mimeType
    when {
        mimeType.isMimeTypeImage() -> MediaImageView(
            localMediaViewState = localMediaViewState,
            localMedia = localMedia,
            modifier = modifier,
            onClick = onClick,
        )
        mimeType.isMimeTypeVideo() -> MediaVideoView(
            isDisplayed = isDisplayed,
            localMediaViewState = localMediaViewState,
            bottomPaddingInPixels = bottomPaddingInPixels,
            localMedia = localMedia,
            autoplay = isUserSelected,
            audioFocus = audioFocus,
            modifier = modifier,
        )
        mimeType == MimeTypes.PlainText -> TextFileView(
            localMedia = localMedia,
            textFileViewer = textFileViewer,
            modifier = modifier,
        )
        mimeType == MimeTypes.Pdf -> MediaPdfView(
            localMediaViewState = localMediaViewState,
            localMedia = localMedia,
            modifier = modifier,
            onClick = onClick,
        )
        mimeType.isMimeTypeAudio() -> MediaAudioView(
            isDisplayed = isDisplayed,
            localMediaViewState = localMediaViewState,
            bottomPaddingInPixels = bottomPaddingInPixels,
            localMedia = localMedia,
            info = mediaInfo,
            audioFocus = audioFocus,
            modifier = modifier,
        )
        else -> MediaFileView(
            localMediaViewState = localMediaViewState,
            uri = localMedia?.uri,
            info = mediaInfo,
            modifier = modifier,
            onOpenWith = onOpenWith,
        )
    }
}
