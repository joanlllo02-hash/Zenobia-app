/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.login.impl.screens.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.appconfig.OnBoardingConfig
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.enterprise.api.canConnectToAnyHomeserver
import com.zenobia.app.features.login.impl.accesscontrol.DefaultAccountProviderAccessControl
import com.zenobia.app.features.login.impl.accountprovider.AccountProviderDataSource
import com.zenobia.app.features.login.impl.login.LoginHelper
import com.zenobia.app.appconfig.DevLoginConfig
import com.zenobia.app.features.rageshake.api.RageshakeFeatureAvailability
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.core.meta.BuildType
import com.zenobia.app.libraries.matrix.api.auth.MatrixAuthenticationService
import com.zenobia.app.libraries.sessionstorage.api.SessionStore
import com.zenobia.app.libraries.ui.utils.MultipleTapToUnlock
import kotlinx.coroutines.launch

@AssistedInject
class OnBoardingPresenter(
    @Assisted private val params: OnBoardingNode.Params,
    private val buildMeta: BuildMeta,
    private val enterpriseService: EnterpriseService,
    private val defaultAccountProviderAccessControl: DefaultAccountProviderAccessControl,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val loginHelper: LoginHelper,
    private val onBoardingLogoResIdProvider: OnBoardingLogoResIdProvider,
    private val sessionStore: SessionStore,
    private val accountProviderDataSource: AccountProviderDataSource,
    private val authenticationService: MatrixAuthenticationService,
) : Presenter<OnBoardingState> {
    @AssistedFactory
    interface Factory {
        fun create(
            params: OnBoardingNode.Params,
        ): OnBoardingPresenter
    }

    private val multipleTapToUnlock = MultipleTapToUnlock()

    @Composable
    override fun present(): OnBoardingState {
        val localCoroutineScope = rememberCoroutineScope()
        val forcedAccountProvider = remember {
            enterpriseService.defaultHomeserverList().singleOrNull()
        }
        val canConnectToAnyHomeserver = remember {
            enterpriseService.canConnectToAnyHomeserver()
        }
        val mustChooseAccountProvider = remember {
            !canConnectToAnyHomeserver && enterpriseService.defaultHomeserverList().size > 1
        }
        val linkAccountProvider by produceState<String?>(initialValue = null) {
            value = params.accountProvider?.takeIf {
                try {
                    defaultAccountProviderAccessControl.assertIsAllowedToConnectToAccountProvider(it, it)
                    true
                } catch (_: Exception) {
                    false
                }
            }
        }
        val defaultAccountProvider = remember(linkAccountProvider) {
            forcedAccountProvider ?: linkAccountProvider
        }
        val canLoginWithQrCode by produceState(initialValue = false, linkAccountProvider) {
            value = linkAccountProvider == null
        }
        val canReportBug by remember { rageshakeFeatureAvailability.isAvailable() }.collectAsState(false)
        var showReportBug by rememberSaveable { mutableStateOf(false) }
        val onBoardingLogoResId = remember {
            onBoardingLogoResIdProvider.get()
        }
        val isAddingAccount by produceState(initialValue = false) {
            value = sessionStore.numberOfSessions() > 0
        }
        val loginMode by loginHelper.collectLoginMode()

        var step by rememberSaveable { mutableStateOf(OnBoardingStep.INTRO_CAROUSEL) }
        var currentSlideIndex by rememberSaveable { mutableStateOf(0) }

        val slides = remember {
            listOf(
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
            )
        }

        fun handleEvent(event: OnBoardingEvents) {
            when (event) {
                is OnBoardingEvents.OnSignIn -> localCoroutineScope.launch {
                    accountProviderDataSource.setUrl(event.defaultAccountProvider)
                    loginHelper.submit(
                        isAccountCreation = false,
                        homeserverUrl = event.defaultAccountProvider,
                        resolvedHomeserverUrl = null,
                        loginHint = params.loginHint?.takeIf { forcedAccountProvider == null },
                    )
                }
                OnBoardingEvents.ClearError -> loginHelper.clearError()
                OnBoardingEvents.OnVersionClick -> {
                    if (canReportBug) {
                        if (multipleTapToUnlock.unlock(localCoroutineScope)) {
                            showReportBug = true
                        }
                    }
                }
                OnBoardingEvents.OnDevSignIn -> localCoroutineScope.launch {
                    devLogin()
                }
                OnBoardingEvents.OnSkipCarousel,
                OnBoardingEvents.OnGetStarted -> {
                    step = OnBoardingStep.WELCOME
                }
                is OnBoardingEvents.OnCarouselSlideChange -> {
                    currentSlideIndex = event.index
                }
                OnBoardingEvents.OnNextSlide -> {
                    if (currentSlideIndex < slides.size - 1) {
                        currentSlideIndex++
                    } else {
                        step = OnBoardingStep.WELCOME
                    }
                }
                OnBoardingEvents.OnBackToCarousel -> {
                    step = OnBoardingStep.INTRO_CAROUSEL
                }
            }
        }

        return OnBoardingState(
            step = step,
            currentSlideIndex = currentSlideIndex,
            slideCount = slides.size,
            slides = slides,
            isAddingAccount = isAddingAccount,
            showBackButton = params.showBackButton,
            showDeveloperSettings = buildMeta.buildType != BuildType.RELEASE,
            showDevLogin = buildMeta.buildType != BuildType.RELEASE,
            productionApplicationName = buildMeta.productionApplicationName,
            defaultAccountProvider = defaultAccountProvider,
            mustChooseAccountProvider = mustChooseAccountProvider,
            canLoginWithQrCode = canLoginWithQrCode,
            canCreateAccount = defaultAccountProvider == null && canConnectToAnyHomeserver && OnBoardingConfig.CAN_CREATE_ACCOUNT,
            canReportBug = canReportBug && showReportBug,
            loginMode = loginMode,
            version = buildMeta.versionName,
            onBoardingLogoResId = onBoardingLogoResId,
            eventSink = ::handleEvent,
        )
    }

    private suspend fun devLogin() {
        authenticationService.setHomeserver(DevLoginConfig.HOMESERVER_URL)
            .onSuccess { details ->
                if (details.supportsPasswordLogin) {
                    authenticationService.login(DevLoginConfig.USERNAME, DevLoginConfig.PASSWORD)
                }
            }
    }
}
