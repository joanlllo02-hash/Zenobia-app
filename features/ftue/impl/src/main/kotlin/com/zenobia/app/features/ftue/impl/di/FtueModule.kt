/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.ftue.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import com.zenobia.app.features.ftue.impl.sessionverification.choosemode.ChooseSelfVerificationModePresenter
import com.zenobia.app.features.ftue.impl.sessionverification.choosemode.ChooseSelfVerificationModeState
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.di.SessionScope

@ContributesTo(SessionScope::class)
@BindingContainer
interface FtueModule {
    @Binds
    fun bindChooseSelfVerificationMethodPresenter(presenter: ChooseSelfVerificationModePresenter): Presenter<ChooseSelfVerificationModeState>
}
