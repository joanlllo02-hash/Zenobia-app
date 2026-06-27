/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.crypto.identity

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.zenobia.app.features.messages.impl.MessagesView
import com.zenobia.app.features.messages.impl.aMessagesState
import com.zenobia.app.features.messages.impl.messagecomposer.aMessageComposerState
import com.zenobia.app.libraries.designsystem.preview.ZenobiaPreview
import com.zenobia.app.libraries.designsystem.preview.PreviewsDayNight
import com.zenobia.app.libraries.textcomposer.model.aTextEditorStateMarkdown

@PreviewsDayNight
@Composable
internal fun MessagesViewWithIdentityChangePreview(
    @PreviewParameter(IdentityChangeStateProvider::class) identityChangeState: IdentityChangeState
) = ZenobiaPreview {
    MessagesView(
        state = aMessagesState(
            composerState = aMessageComposerState(
                textEditorState = aTextEditorStateMarkdown(
                    initialText = "",
                    initialFocus = false,
                )
            ),
            identityChangeState = identityChangeState,
        ),
        onBackClick = {},
        onRoomDetailsClick = {},
        onEventContentClick = { _, _ -> false },
        onUserDataClick = {},
        onLinkClick = { _, _ -> },
        onSendLocationClick = {},
        onCreatePollClick = {},
        onJoinCallClick = {},
        onViewAllPinnedMessagesClick = {},
        knockRequestsBannerView = {},
        onThreadsListClick = {},
    )
}
