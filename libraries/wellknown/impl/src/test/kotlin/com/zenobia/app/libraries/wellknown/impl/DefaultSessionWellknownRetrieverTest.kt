/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.zenobia.app.libraries.wellknown.impl

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.features.wellknown.test.anElementWellKnown
import com.zenobia.app.libraries.androidutils.json.DefaultJsonProvider
import com.zenobia.app.libraries.androidutils.json.JsonProvider
import com.zenobia.app.libraries.cachestore.api.CacheData
import com.zenobia.app.libraries.cachestore.api.CacheStore
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.sessionstorage.test.InMemoryCacheStore
import com.zenobia.app.libraries.wellknown.api.CustomRecoveryPassphrase
import com.zenobia.app.libraries.wellknown.api.ElementWellKnown
import com.zenobia.app.libraries.wellknown.api.WellknownRetrieverResult
import com.zenobia.app.services.toolbox.api.systemclock.SystemClock
import com.zenobia.app.services.toolbox.test.systemclock.A_FAKE_TIMESTAMP
import com.zenobia.app.services.toolbox.test.systemclock.FakeSystemClock
import com.zenobia.app.tests.testutils.lambda.lambdaError
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultSessionWellknownRetrieverTest {
    @Test
    fun `get empty element wellknown`() = runTest {
        val getUrlLambda = lambdaRecorder<String, Result<ByteArray>> {
            Result.success("{}".toByteArray())
        }
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = getUrlLambda,
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                ElementWellKnown(
                    registrationHelperUrl = null,
                    enforceElementPro = null,
                    rageshakeUrl = null,
                    brandColor = null,
                    notificationSound = null,
                    identityProviderAppScheme = null,
                    customRecoveryPassphrase = null,
                )
            )
        )
        getUrlLambda.assertions().isCalledOnce()
            .with(value("https://user.domain.org/.well-known/element/element.json"))
    }

    @Test
    fun `get element wellknown with full content`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success(
                    WELLKNOWN_CONTENT.toByteArray()
                )
            }
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                ElementWellKnown(
                    registrationHelperUrl = "a_registration_url",
                    enforceElementPro = true,
                    rageshakeUrl = "a_rageshake_url",
                    brandColor = "#FF0000",
                    notificationSound = "a_notification_sound.flac",
                    identityProviderAppScheme = "an_app_scheme",
                    customRecoveryPassphrase = null,
                )
            )
        )
    }

    @Test
    fun `get element wellknown with unknown key`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success(
                    """{
                    "registration_helper_url": "a_registration_url",
                    "enforce_element_pro": true,
                    "rageshake_url": "a_rageshake_url",
                    // Note the trailing comma, and the comment!
                    "other": true,
                }""".trimIndent().toByteArray()
                )
            },
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                ElementWellKnown(
                    registrationHelperUrl = "a_registration_url",
                    enforceElementPro = true,
                    rageshakeUrl = "a_rageshake_url",
                    brandColor = null,
                    notificationSound = null,
                    identityProviderAppScheme = null,
                    customRecoveryPassphrase = null,
                )
            )
        )
    }

    @Test
    fun `get element wellknown with custom recovery passphrase settings`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success(
                    """{
                    "custom_recovery_passphrase": {
                        "min_character_count": 8
                    }
                }""".trimIndent().toByteArray()
                )
            },
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                anElementWellKnown(
                    customRecoveryPassphrase = CustomRecoveryPassphrase(minCharacterCount = 8)
                )
            )
        )
    }

    @Test
    fun `get element wellknown with custom recovery passphrase settings missing min character count floors to 1`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success(
                    """{
                    "custom_recovery_passphrase": {}
                }""".trimIndent().toByteArray()
                )
            },
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                anElementWellKnown(
                    customRecoveryPassphrase = CustomRecoveryPassphrase(minCharacterCount = 1)
                )
            )
        )
    }

    @Test
    fun `get element wellknown with zero min character count floors to 1`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success(
                    """{
                    "custom_recovery_passphrase": {
                        "min_character_count": 0
                    }
                }""".trimIndent().toByteArray()
                )
            },
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                anElementWellKnown(
                    customRecoveryPassphrase = CustomRecoveryPassphrase(minCharacterCount = 1)
                )
            )
        )
    }

    @Test
    fun `get element wellknown with negative min character count floors to 1`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success(
                    """{
                    "custom_recovery_passphrase": {
                        "min_character_count": -5
                    }
                }""".trimIndent().toByteArray()
                )
            },
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                anElementWellKnown(
                    customRecoveryPassphrase = CustomRecoveryPassphrase(minCharacterCount = 1)
                )
            )
        )
    }

    @Test
    fun `get element wellknown json error`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success(
                    """{
                    "registration_helper_url" = "a_registration_url",
                    error
                }""".trimIndent().toByteArray()
                )
            }
        )
        assertThat(sut.getElementWellKnown()).isInstanceOf(WellknownRetrieverResult.Error::class.java)
    }

    @Test
    fun `get element wellknown network error`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.failure(AN_EXCEPTION)
            }
        )
        assertThat(sut.getElementWellKnown()).isInstanceOf(WellknownRetrieverResult.Error::class.java)
    }

    @Test
    fun `get element wellknown hitting cache`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = { lambdaError() },
            cacheStore = InMemoryCacheStore(
                initialData = mapOf(
                    WELLKNOWN_URL to CacheData(
                        value = WELLKNOWN_CONTENT,
                        updatedAt = A_FAKE_TIMESTAMP,
                    )
                )
            )
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                ElementWellKnown(
                    registrationHelperUrl = "a_registration_url",
                    enforceElementPro = true,
                    rageshakeUrl = "a_rageshake_url",
                    brandColor = "#FF0000",
                    notificationSound = "a_notification_sound.flac",
                    identityProviderAppScheme = "an_app_scheme",
                    customRecoveryPassphrase = null,
                )
            )
        )
    }

    @Test
    fun `get element wellknown hitting cache containing invalid json`() = runTest {
        val cacheStore = InMemoryCacheStore(
            initialData = mapOf(
                WELLKNOWN_URL to CacheData(
                    value = WELLKNOWN_CONTENT,
                    updatedAt = A_FAKE_TIMESTAMP,
                )
            )
        )
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success("{}".toByteArray())
            },
            cacheStore = cacheStore,
            jsonProvider = JsonProvider { error("Failed to parse JSON") }
        )
        assertThat(sut.getElementWellKnown()).isInstanceOf(WellknownRetrieverResult.Error::class.java)
        // Ensure that the cache is deleted after the failure to parse it
        assertThat(cacheStore.dataMap).isEmpty()
    }

    @Test
    fun `get element wellknown hitting outdated cache`() = runTest {
        val sut = createDefaultSessionWellknownRetriever(
            getUrlLambda = {
                Result.success("{}".toByteArray())
            },
            cacheStore = InMemoryCacheStore(
                initialData = mapOf(
                    WELLKNOWN_URL to CacheData(
                        value = WELLKNOWN_CONTENT,
                        updatedAt = 0L,
                    )
                ),
            ),
            // 3 days later, so the cache is outdated
            systemClock = FakeSystemClock(3 * 24 * 60 * 60 * 1000L)
        )
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                ElementWellKnown(
                    registrationHelperUrl = "a_registration_url",
                    enforceElementPro = true,
                    rageshakeUrl = "a_rageshake_url",
                    brandColor = "#FF0000",
                    notificationSound = "a_notification_sound.flac",
                    identityProviderAppScheme = "an_app_scheme",
                    customRecoveryPassphrase = null,
                )
            )
        )
        // Next call returns the updated value
        runCurrent()
        assertThat(sut.getElementWellKnown()).isEqualTo(
            WellknownRetrieverResult.Success(
                anElementWellKnown()
            )
        )
    }

    private fun TestScope.createDefaultSessionWellknownRetriever(
        getUrlLambda: (String) -> Result<ByteArray>,
        jsonProvider: JsonProvider = DefaultJsonProvider(),
        cacheStore: CacheStore = InMemoryCacheStore(),
        systemClock: SystemClock = FakeSystemClock(),
    ) = DefaultSessionWellknownRetriever(
        matrixClient = FakeMatrixClient(
            userIdServerNameLambda = { "user.domain.org" },
            getUrlLambda = getUrlLambda,
        ),
        json = jsonProvider,
        cacheStore = cacheStore,
        systemClock = systemClock,
        sessionCoroutineScope = backgroundScope,
    )

    companion object {
        private const val WELLKNOWN_URL = "https://user.domain.org/.well-known/element/element.json"
        private const val WELLKNOWN_CONTENT = """{
                "registration_helper_url": "a_registration_url",
                "enforce_element_pro": true,
                "rageshake_url": "a_rageshake_url",
                "brand_color": "#FF0000",
                "notification_sound": "a_notification_sound.flac",
                "idp_app_scheme": "an_app_scheme"
            }"""
    }
}
