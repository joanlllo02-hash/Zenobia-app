/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.api

import com.zenobia.app.libraries.textcomposer.model.MessageComposerMode

/**
 * Hoist-able state of the message composer.
 *
 * Typical use case is inside other presenters, to know if
 * the composer is in a thread, if it's editing a message, etc.
 */
interface MessageComposerContext {
    val composerMode: MessageComposerMode
}
