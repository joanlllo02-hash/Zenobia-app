/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.developer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import dev.zacsweers.metro.Inject
import com.zenobia.app.features.enterprise.api.EnterpriseService
import com.zenobia.app.features.preferences.impl.developer.appsettings.AppDeveloperSettingsState
import com.zenobia.app.features.preferences.impl.tasks.ClearCacheUseCase
import com.zenobia.app.features.preferences.impl.tasks.ComputeCacheSizeUseCase
import com.zenobia.app.features.preferences.impl.tasks.VacuumStoresUseCase
import com.zenobia.app.libraries.androidutils.filesize.FileSizeFormatter
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.architecture.Presenter
import com.zenobia.app.libraries.architecture.runCatchingUpdatingState
import com.zenobia.app.libraries.core.data.ByteUnit
import com.zenobia.app.libraries.matrix.api.analytics.GetDatabaseSizesUseCase
import com.zenobia.app.libraries.matrix.api.core.SessionId
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Inject
class DeveloperSettingsPresenter(
    private val appDeveloperSettingsPresenter: Presenter<AppDeveloperSettingsState>,
    private val sessionId: SessionId,
    private val computeCacheSizeUseCase: ComputeCacheSizeUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val enterpriseService: EnterpriseService,
    private val vacuumStoresUseCase: VacuumStoresUseCase,
    private val databaseSizesUseCase: GetDatabaseSizesUseCase,
    private val fileSizeFormatter: FileSizeFormatter,
) : Presenter<DeveloperSettingsState> {
    @Composable
    override fun present(): DeveloperSettingsState {
        val cacheSize = remember {
            mutableStateOf<AsyncData<String>>(AsyncData.Uninitialized)
        }
        val databaseSizes = remember {
            mutableStateOf<AsyncData<ImmutableMap<String, String>>>(AsyncData.Uninitialized)
        }
        val clearCacheAction = remember {
            mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized)
        }
        var showColorPicker by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(Unit) {
            computeDatabaseSizes(databaseSizes)
        }
        val coroutineScope = rememberCoroutineScope()
        // Compute cache size each time the clear cache action value is changed
        LaunchedEffect(clearCacheAction.value.isSuccess()) {
            computeCacheSize(cacheSize)
        }

        fun handleEvent(event: DeveloperSettingsEvents) {
            when (event) {
                DeveloperSettingsEvents.ClearCache -> coroutineScope.clearCache(clearCacheAction)
                is DeveloperSettingsEvents.ChangeBrandColor -> coroutineScope.launch {
                    showColorPicker = false
                    val color = event.color
                        ?.toArgb()
                        ?.toHexString(HexFormat.UpperCase)
                        ?.substring(2, 8)
                        ?.padStart(7, '#')
                    enterpriseService.overrideBrandColor(sessionId, color)
                }
                is DeveloperSettingsEvents.SetShowColorPicker -> {
                    showColorPicker = event.show
                }
                DeveloperSettingsEvents.VacuumStores -> coroutineScope.launch {
                    vacuumStoresUseCase()
                }
            }
        }

        val appDeveloperSettingsState = appDeveloperSettingsPresenter.present()
        return DeveloperSettingsState(
            appDeveloperSettingsState = appDeveloperSettingsState,
            cacheSize = cacheSize.value,
            databaseSizes = databaseSizes.value,
            clearCacheAction = clearCacheAction.value,
            isEnterpriseBuild = enterpriseService.isEnterpriseBuild,
            showColorPicker = showColorPicker,
            eventSink = ::handleEvent,
        )
    }

    private fun CoroutineScope.computeCacheSize(cacheSize: MutableState<AsyncData<String>>) = launch {
        suspend {
            computeCacheSizeUseCase()
        }.runCatchingUpdatingState(cacheSize)
    }

    private fun CoroutineScope.computeDatabaseSizes(databaseSizes: MutableState<AsyncData<ImmutableMap<String, String>>>) = launch {
        suspend {
            databaseSizesUseCase(sessionId).getOrThrow().let { sizes ->
                buildMap {
                    sizes.stateStore?.let { stateStoreSize ->
                        put("State store", fileSizeFormatter.format(stateStoreSize.into(ByteUnit.BYTES), useShortFormat = true))
                    }
                    sizes.eventCacheStore?.let { eventCacheStoreSize ->
                        put("Event cache store", fileSizeFormatter.format(eventCacheStoreSize.into(ByteUnit.BYTES), useShortFormat = true))
                    }
                    sizes.mediaStore?.let { mediaStoreSize ->
                        put("Media store", fileSizeFormatter.format(mediaStoreSize.into(ByteUnit.BYTES), useShortFormat = true))
                    }
                    sizes.cryptoStore?.let { cryptoStoreSize ->
                        put("Crypto store", fileSizeFormatter.format(cryptoStoreSize.into(ByteUnit.BYTES), useShortFormat = true))
                    }
                }
            }.toImmutableMap()
        }.runCatchingUpdatingState(databaseSizes)
    }

    private fun CoroutineScope.clearCache(clearCacheAction: MutableState<AsyncAction<Unit>>) = launch {
        suspend {
            clearCacheUseCase()
        }.runCatchingUpdatingState(clearCacheAction)
    }
}
