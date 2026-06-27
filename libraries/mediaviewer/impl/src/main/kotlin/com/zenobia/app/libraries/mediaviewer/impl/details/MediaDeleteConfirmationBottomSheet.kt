/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.zenobia.app.compound.theme.ZenobiaTheme
import com.zenobia.app.compound.tokens.generated.CompoundIcons
import com.zenobia.app.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import com.zenobia.app.libraries.designsystem.components.BigIcon
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.designsystem.theme.components.Button
import com.zenobia.app.libraries.designsystem.theme.components.ModalBottomSheet
import com.zenobia.app.libraries.designsystem.theme.components.TextButton
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.matrix.ui.media.MediaRequestData
import com.zenobia.app.libraries.mediaviewer.impl.R
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.libraries.ui.strings.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDeleteConfirmationBottomSheet(
    state: MediaBottomSheetState.DeleteConfirmation,
    onDelete: (EventId) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        scrollable = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            IconTitleSubtitleMolecule(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                title = stringResource(R.string.screen_media_browser_delete_confirmation_title),
                iconStyle = BigIcon.Style.Default(CompoundIcons.Delete(), useCriticalTint = true),
                subTitle = stringResource(R.string.screen_media_browser_delete_confirmation_subtitle),
            )
            Spacer(modifier = Modifier.height(16.dp))
            MediaRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = state,
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                text = stringResource(CommonStrings.action_remove),
                onClick = {
                    onDelete(state.eventId)
                },
                destructive = true,
            )
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                text = stringResource(CommonStrings.action_cancel),
                onClick = {
                    onDismiss()
                },
            )
        }
    }
}

@Composable
private fun MediaRow(
    state: MediaBottomSheetState.DeleteConfirmation,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp),
        ) {
            if (state.thumbnailSource == null) {
                BigIcon(
                    style = BigIcon.Style.Default(CompoundIcons.Attachment()),
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    model = MediaRequestData(state.thumbnailSource, MediaRequestData.Kind.Thumbnail(100)),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    contentDescription = null,
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
        ) {
            // Name
            Text(
                modifier = Modifier.clipToBounds(),
                text = state.mediaInfo.filename,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = ZenobiaTheme.typography.fontBodyLgRegular,
            )
            // Info
            Text(
                text = state.mediaInfo.mimeType + Strings.NICE_SEPARATOR + state.mediaInfo.formattedFileSize,
                color = ZenobiaTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = ZenobiaTheme.typography.fontBodySmRegular,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun MediaDeleteConfirmationBottomSheetPreview(
    @PreviewParameter(provider = MediaBottomSheetStateDeleteConfirmationProvider::class) state: MediaBottomSheetState.DeleteConfirmation,
) = ZenobiaPreview {
    MediaDeleteConfirmationBottomSheet(
        state = state,
        onDelete = {},
        onDismiss = {},
    )
}
