/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.gallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.core.extensions.withBrackets
import com.zenobia.app.libraries.designsystem.modifiers.onKeyboardContextMenuAction
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.HorizontalDivider
import com.zenobia.app.libraries.designsystem.theme.components.Icon
import com.zenobia.app.libraries.designsystem.theme.components.Text
import com.zenobia.app.libraries.mediaviewer.impl.model.MediaItem
import com.zenobia.app.libraries.ui.strings.CommonStrings

@Composable
fun AudioItemView(
    audio: MediaItem.Audio,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        FilenameRow(
            audio = audio,
            onClick = onClick,
            onLongClick = onLongClick,
        )
        val caption = audio.mediaInfo.caption
        if (caption != null) {
            CaptionView(caption)
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }
        HorizontalDivider()
    }
}

@Composable
private fun FilenameRow(
    audio: MediaItem.Audio,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = ZenobiaTheme.colors.bgSubtleSecondary,
                shape = RoundedCornerShape(12.dp),
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onLongClickLabel = stringResource(CommonStrings.action_open_context_menu),
            )
            .onKeyboardContextMenuAction(onLongClick)
            .fillMaxWidth()
            .padding(start = 12.dp, end = 36.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .background(
                    color = ZenobiaTheme.colors.bgActionSecondaryRest,
                    shape = CircleShape,
                )
                .size(32.dp)
                .padding(6.dp),
            imageVector = CompoundIcons.Audio(),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = audio.mediaInfo.filename,
            modifier = Modifier.weight(1f),
            style = ZenobiaTheme.typography.fontBodyLgRegular,
            color = ZenobiaTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        val formattedSize = audio.mediaInfo.formattedFileSize
        if (formattedSize.isNotEmpty()) {
            Text(
                text = formattedSize.withBrackets(),
                style = ZenobiaTheme.typography.fontBodyLgRegular,
                color = ZenobiaTheme.colors.textPrimary,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun AudioItemViewPreview(
    @PreviewParameter(MediaItemAudioProvider::class) audio: MediaItem.Audio,
) = ZenobiaPreview {
    AudioItemView(
        audio = audio,
        onClick = {},
        onLongClick = {},
    )
}
