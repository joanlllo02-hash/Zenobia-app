/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.zenobia.app.features.login.impl.login.LoginMode
import com.zenobia.app.libraries.architecture.AsyncData

enum class OnBoardingStep {
    INTRO_CAROUSEL,
    WELCOME,
}

data class IntroSlide(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val accentColor: Long,
)

data class OnBoardingState(
    val step: OnBoardingStep,
    val currentSlideIndex: Int,
    val slideCount: Int,
    val slides: List<IntroSlide>,
    val isAddingAccount: Boolean,
    val showBackButton: Boolean,
    val showDeveloperSettings: Boolean,
    val showDevLogin: Boolean,
    val productionApplicationName: String,
    val defaultAccountProvider: String?,
    val mustChooseAccountProvider: Boolean,
    val canLoginWithQrCode: Boolean,
    val canCreateAccount: Boolean,
    val canReportBug: Boolean,
    val version: String,
    @DrawableRes
    val onBoardingLogoResId: Int?,
    val loginMode: AsyncData<LoginMode>,
    val eventSink: (OnBoardingEvents) -> Unit,
) {
    val submitEnabled: Boolean
        get() = defaultAccountProvider != null && (loginMode is AsyncData.Uninitialized || loginMode is AsyncData.Loading)
}
