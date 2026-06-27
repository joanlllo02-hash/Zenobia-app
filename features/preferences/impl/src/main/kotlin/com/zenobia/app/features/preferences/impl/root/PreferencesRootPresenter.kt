/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import com.zenobia.app.features.enterprise.api.SessionEnterpriseService
import com.zenobia.app.features.logout.api.direct.DirectLogoutState
import com.zenobia.app.features.preferences.impl.utils.ShowDeveloperSettingsProvider
import com.zenobia.app.features.rageshake.api.RageshakeFeatureAvailability
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import com.zenobia.app.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import com.zenobia.app.libraries.featureflag.api.FeatureFlagService
import com.zenobia.app.libraries.featureflag.api.FeatureFlags
import com.zenobia.app.libraries.indicator.api.IndicatorService
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.user.MatrixUser
import com.zenobia.app.libraries.matrix.api.verification.SessionVerificationService
import com.zenobia.app.libraries.sessionstorage.api.SessionStore
import com.zenobia.app.services.analytics.api.AnalyticsService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Inject
class PreferencesRootPresenter(
    private val matrixClient: MatrixClient,
    private val sessionVerificationService: SessionVerificationService,
    private val analyticsService: AnalyticsService,
    private val versionFormatter: VersionFormatter,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val indicatorService: IndicatorService,
    private val directLogoutPresenter: Presenter<DirectLogoutState>,
    private val showDeveloperSettingsProvider: ShowDeveloperSettingsProvider,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val featureFlagService: FeatureFlagService,
    private val sessionStore: SessionStore,
    private val sessionEnterpriseService: SessionEnterpriseService,
) : Presenter<PreferencesRootState> {
    @Composable
    override fun present(): PreferencesRootState {
        val coroutineScope = rememberCoroutineScope()
        val matrixUser = matrixClient.userProfile.collectAsState()
        LaunchedEffect(Unit) {
            // Force a refresh of the profile
            matrixClient.getUserProfile()
        }

        val isMultiAccountEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.MultiAccount)
        }.collectAsState(initial = false)
        val showLinkNewDevice by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.QrCodeLogin)
        }.collectAsState(initial = false)

        val otherSessions by remember {
            sessionStore.sessionsFlow().map { list ->
                list
                    .filter { it.userId != matrixClient.sessionId.value }
                    .map {
                        MatrixUser(
                            userId = UserId(it.userId),
                            displayName = it.userDisplayName,
                            avatarUrl = it.userAvatarUrl,
                        )
                    }
                    .toImmutableList()
            }
        }.collectAsState(initial = persistentListOf())

        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()
        val hasAnalyticsProviders = remember { analyticsService.getAvailableAnalyticsProviders().isNotEmpty() }

        // We should display the 'complete verification' option if the current session can be verified
        val canVerifyUserSession by sessionVerificationService.needsSessionVerification.collectAsState(false)

        val showSecureBackupIndicator by indicatorService.showSettingChatBackupIndicator()

        val accountManagementUrl: MutableState<String?> = remember {
            mutableStateOf(null)
        }
        var canDeactivateAccount by remember {
            mutableStateOf(false)
        }
        val canReportBug by remember { rageshakeFeatureAvailability.isAvailable() }.collectAsState(false)
        LaunchedEffect(Unit) {
            canDeactivateAccount = matrixClient.canDeactivateAccount()
        }

        val nbOfBlockedUsers by produceState(initialValue = 0) {
            matrixClient.ignoredUsersFlow
                .onEach { value = it.size }
                .launchIn(this)
        }

        val showLabsItem = remember { featureFlagService.getAvailableFeatures(isInLabs = true).isNotEmpty() }

        val directLogoutState = directLogoutPresenter.present()

        LaunchedEffect(Unit) {
            initAccountManagementUrl(accountManagementUrl)
        }

        val showDeveloperSettings by showDeveloperSettingsProvider.showDeveloperSettings.collectAsState()

        fun handleEvent(event: PreferencesRootEvent) {
            when (event) {
                is PreferencesRootEvent.OnVersionInfoClick -> {
                    showDeveloperSettingsProvider.unlockDeveloperSettings(coroutineScope)
                }
                is PreferencesRootEvent.SwitchToSession -> coroutineScope.launch {
                    sessionStore.setLatestSession(event.sessionId.value)
                }
            }
        }

        return PreferencesRootState(
            myUser = matrixUser.value,
            version = remember { versionFormatter.get() },
            deviceId = matrixClient.deviceId,
            isMultiAccountEnabled = isMultiAccountEnabled,
            otherSessions = otherSessions,
            showSecureBackup = !canVerifyUserSession,
            showSecureBackupBadge = showSecureBackupIndicator,
            accountManagementUrl = accountManagementUrl.value,
            showAnalyticsSettings = hasAnalyticsProviders,
            canReportBug = canReportBug,
            showLinkNewDevice = showLinkNewDevice,
            showDeveloperSettings = showDeveloperSettings,
            canDeactivateAccount = canDeactivateAccount,
            nbOfBlockedUsers = nbOfBlockedUsers,
            showLabsItem = showLabsItem,
            directLogoutState = directLogoutState,
            snackbarMessage = snackbarMessage,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.initAccountManagementUrl(
        accountManagementUrl: MutableState<String?>,
    ) = launch {
        accountManagementUrl.value = matrixClient.getAccountManagementUrl(null)
            .getOrNull()
            ?.let {
                sessionEnterpriseService.tweakMasUrl(it)
            }
    }
}
