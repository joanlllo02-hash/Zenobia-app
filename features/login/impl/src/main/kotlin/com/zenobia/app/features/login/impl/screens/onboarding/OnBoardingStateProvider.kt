/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.login.impl.login.LoginMode
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.R

open class OnBoardingStateProvider : PreviewParameterProvider<OnBoardingState> {
    override val values: Sequence<OnBoardingState>
        get() = sequenceOf(
            anOnBoardingState(),
            anOnBoardingState(
                step = OnBoardingStep.WELCOME,
                canLoginWithQrCode = true,
                canCreateAccount = true,
            ),
            anOnBoardingState(
                step = OnBoardingStep.WELCOME,
                canLoginWithQrCode = true,
                canCreateAccount = true,
                canReportBug = true,
            ),
            anOnBoardingState(
                step = OnBoardingStep.WELCOME,
                defaultAccountProvider = "matrix.org",
                canCreateAccount = false,
                canReportBug = true,
            ),
            anOnBoardingState(
                step = OnBoardingStep.WELCOME,
                customLogoResId = R.drawable.sample_background,
            ),
            anOnBoardingState(
                step = OnBoardingStep.WELCOME,
                isAddingAccount = true,
                canLoginWithQrCode = true,
                canCreateAccount = true,
            ),
            anOnBoardingState(
                step = OnBoardingStep.INTRO_CAROUSEL,
                currentSlideIndex = 0,
            ),
            anOnBoardingState(
                step = OnBoardingStep.INTRO_CAROUSEL,
                currentSlideIndex = 1,
            ),
            anOnBoardingState(
                step = OnBoardingStep.INTRO_CAROUSEL,
                currentSlideIndex = 2,
            ),
        )
}

fun anOnBoardingState(
    step: OnBoardingStep = OnBoardingStep.WELCOME,
    currentSlideIndex: Int = 0,
    isAddingAccount: Boolean = false,
    showBackButton: Boolean = false,
    showDeveloperSettings: Boolean = false,
    showDevLogin: Boolean = false,
    productionApplicationName: String = "Zenobia",
    defaultAccountProvider: String? = null,
    mustChooseAccountProvider: Boolean = false,
    canLoginWithQrCode: Boolean = false,
    canCreateAccount: Boolean = false,
    canReportBug: Boolean = false,
    version: String = "1.0.0",
    @DrawableRes
    customLogoResId: Int? = null,
    loginMode: AsyncData<LoginMode> = AsyncData.Uninitialized,
    eventSink: (OnBoardingEvents) -> Unit = {},
) = OnBoardingState(
    step = step,
    currentSlideIndex = currentSlideIndex,
    slideCount = 3,
    slides = listOf(
        IntroSlide(
            icon = Icons.Default.Lock,
            title = "الخصوصية والأمان",
            description = "تشفير كامل من البداية إلى النهاية. أنت فقط من يملك مفتاح محادثاتك.",
            accentColor = 0xFF0DBDA8,
        ),
        IntroSlide(
            icon = Icons.Default.Language,
            title = "تواصل لا مركزي",
            description = "لا خوادم مركزية. شبكة Matrix الموزعة تمنحك الحرية الكاملة لبياناتك.",
            accentColor = 0xFF0D5CBD,
        ),
        IntroSlide(
            icon = Icons.Default.Star,
            title = "ميزات متقدمة",
            description = "غرف، مساحات، مكالمات صوت ومرئي، مشاركة ملفات، بوتات ذكية والمزيد.",
            accentColor = 0xFF8B5CF6,
        ),
    ),
    isAddingAccount = isAddingAccount,
    showBackButton = showBackButton,
    showDeveloperSettings = showDeveloperSettings,
    showDevLogin = showDevLogin,
    productionApplicationName = productionApplicationName,
    defaultAccountProvider = defaultAccountProvider,
    mustChooseAccountProvider = mustChooseAccountProvider,
    canLoginWithQrCode = canLoginWithQrCode,
    canCreateAccount = canCreateAccount,
    canReportBug = canReportBug,
    version = version,
    loginMode = loginMode,
    onBoardingLogoResId = customLogoResId,
    eventSink = eventSink,
)
