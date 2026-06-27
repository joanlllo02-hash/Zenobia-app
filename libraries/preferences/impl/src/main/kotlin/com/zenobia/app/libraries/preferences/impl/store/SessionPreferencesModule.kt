/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.preferences.impl.store

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.di.annotations.SessionCoroutineScope
import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.preferences.api.store.SessionPreferencesStore
import kotlinx.coroutines.CoroutineScope

@BindingContainer
@ContributesTo(SessionScope::class)
object SessionPreferencesModule {
    @Provides
    fun providesSessionPreferencesStore(
        defaultSessionPreferencesStoreFactory: DefaultSessionPreferencesStoreFactory,
        sessionId: SessionId,
        @SessionCoroutineScope sessionCoroutineScope: CoroutineScope,
    ): SessionPreferencesStore {
        return defaultSessionPreferencesStoreFactory
            .get(sessionId, sessionCoroutineScope)
    }
}
