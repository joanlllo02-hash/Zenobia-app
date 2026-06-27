/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.userprofile.impl

import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.features.userprofile.api.UserProfilePresenterFactory
import com.zenobia.app.features.userprofile.api.UserProfileState
import com.zenobia.app.features.userprofile.impl.root.UserProfilePresenter
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.core.UserId

@ContributesBinding(SessionScope::class)
class DefaultUserProfilePresenterFactory(
    private val factory: UserProfilePresenter.Factory,
) : UserProfilePresenterFactory {
    override fun create(userId: UserId): Presenter<UserProfileState> = factory.create(userId)
}
