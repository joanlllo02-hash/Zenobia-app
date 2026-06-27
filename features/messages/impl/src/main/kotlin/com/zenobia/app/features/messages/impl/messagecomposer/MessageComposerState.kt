/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.messagecomposer

import androidx.compose.runtime.Stable
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.textcomposer.mentions.ResolvedSuggestion
import com.zenobia.app.libraries.textcomposer.model.MessageComposerMode
import com.zenobia.app.libraries.textcomposer.model.TextEditorState
import com.zenobia.app.wysiwyg.display.TextDisplay
import kotlinx.collections.immutable.ImmutableList

@Stable
data class MessageComposerState(
    val textEditorState: TextEditorState,
    val isFullScreen: Boolean,
    val mode: MessageComposerMode,
    val showAttachmentSourcePicker: Boolean,
    val showTextFormatting: Boolean,
    val canShareLocation: Boolean,
    val suggestions: ImmutableList<ResolvedSuggestion>,
    val resolveMentionDisplay: (String, String) -> TextDisplay,
    val resolveAtRoomMentionDisplay: () -> TextDisplay,
    val slashCommandAction: AsyncAction<Unit>,
    val eventSink: (MessageComposerEvent) -> Unit,
)
