/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.fullscreenintent.impl.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import com.zenobia.app.libraries.fullscreenintent.impl.FullScreenIntentPermissionsPresenter

@ContributesTo(AppScope::class)
@BindingContainer
interface FullScreenIntentModule {
    @Binds
    fun bindFullScreenIntentPermissionsPresenter(presenter: FullScreenIntentPermissionsPresenter): Presenter<FullScreenIntentPermissionsState>
}
