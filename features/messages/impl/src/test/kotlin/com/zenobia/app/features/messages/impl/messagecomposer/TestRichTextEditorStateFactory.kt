/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.messagecomposer

import androidx.compose.runtime.Composable
import com.zenobia.app.wysiwyg.compose.RichTextEditorState
import com.zenobia.app.wysiwyg.compose.rememberRichTextEditorState

class TestRichTextEditorStateFactory : RichTextEditorStateFactory {
    @Composable
    override fun remember(): RichTextEditorState {
        return rememberRichTextEditorState("", fake = true)
    }
}
