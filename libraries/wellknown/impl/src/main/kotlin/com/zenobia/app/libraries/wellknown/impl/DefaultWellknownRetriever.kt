/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.wellknown.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.core.extensions.runCatchingExceptions
import com.zenobia.app.libraries.core.uri.ensureProtocol
import com.zenobia.app.libraries.network.RetrofitFactory
import com.zenobia.app.libraries.wellknown.api.ElementWellKnown
import com.zenobia.app.libraries.wellknown.api.WellknownRetriever
import com.zenobia.app.libraries.wellknown.api.WellknownRetrieverResult
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection

@ContributesBinding(AppScope::class)
class DefaultWellknownRetriever(
    private val retrofitFactory: RetrofitFactory,
) : WellknownRetriever {
    override suspend fun getElementWellKnown(baseUrl: String): WellknownRetrieverResult<ElementWellKnown> {
        return buildWellknownApi(baseUrl)
            .map { wellknownApi ->
                try {
                    val result = wellknownApi.getElementWellKnown().map()
                    WellknownRetrieverResult.Success(result)
                } catch (e: Exception) {
                    // Is it a 404?
                    Timber.e(e, "Failed to retrieve Element well-known data for $baseUrl")
                    if ((e as? HttpException)?.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                        WellknownRetrieverResult.NotFound
                    } else {
                        WellknownRetrieverResult.Error(e)
                    }
                }
            }
            .fold(
                onSuccess = { it },
                onFailure = { WellknownRetrieverResult.Error(it as Exception) }
            )
    }

    private fun buildWellknownApi(accountProviderUrl: String): Result<WellknownAPI> {
        return runCatchingExceptions {
            retrofitFactory.create(accountProviderUrl.ensureProtocol())
                .create(WellknownAPI::class.java)
        }.onFailure { e ->
            // If the base URL is not valid, we cannot retrieve the well-known data
            Timber.e(e, "Failed to create Retrofit instance for $accountProviderUrl")
        }
    }
}
