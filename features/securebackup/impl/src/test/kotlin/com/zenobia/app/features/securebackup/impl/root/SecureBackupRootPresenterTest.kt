/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.securebackup.impl.root

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.zenobia.app.libraries.architecture.AsyncAction
import com.zenobia.app.libraries.architecture.AsyncData
import com.zenobia.app.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import com.zenobia.app.libraries.matrix.api.encryption.BackupState
import com.zenobia.app.libraries.matrix.api.encryption.EncryptionService
import com.zenobia.app.libraries.matrix.api.encryption.RecoveryState
import com.zenobia.app.libraries.matrix.test.AN_EXCEPTION
import com.zenobia.app.libraries.matrix.test.core.aBuildMeta
import com.zenobia.app.libraries.matrix.test.encryption.FakeEncryptionService
import com.zenobia.app.tests.testutils.WarmUpRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SecureBackupRootPresenterTest {
    @get:Rule
    val warmUpRule = WarmUpRule()

    @Test
    fun `present - initial state`() = runTest {
        val presenter = createSecureBackupRootPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(2)
            val initialState = awaitItem()
            assertThat(initialState.backupState).isEqualTo(BackupState.UNKNOWN)
            assertThat(initialState.doesBackupExistOnServer.dataOrNull()).isTrue()
            assertThat(initialState.enableAction).isEqualTo(AsyncAction.Uninitialized)
            assertThat(initialState.displayKeyStorageDisabledError).isFalse()
            assertThat(initialState.recoveryState).isEqualTo(RecoveryState.UNKNOWN)
            assertThat(initialState.appName).isEqualTo("Zenobia")
            assertThat(initialState.snackbarMessage).isNull()
        }
    }

    @Test
    fun `present - Unknown state`() = runTest {
        val encryptionService = FakeEncryptionService()
        val presenter = createSecureBackupRootPresenter(
            encryptionService = encryptionService,
        )
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            val initialState = awaitItem()
            encryptionService.givenDoesBackupExistOnServerResult(Result.failure(AN_EXCEPTION))
            assertThat(initialState.backupState).isEqualTo(BackupState.UNKNOWN)
            assertThat(initialState.doesBackupExistOnServer).isEqualTo(AsyncData.Uninitialized)
            val loadingState1 = awaitItem()
            assertThat(loadingState1.doesBackupExistOnServer).isInstanceOf(AsyncData.Loading::class.java)
            val errorState = awaitItem()
            assertThat(errorState.doesBackupExistOnServer).isEqualTo(AsyncData.Failure<Boolean>(AN_EXCEPTION))
            encryptionService.givenDoesBackupExistOnServerResult(Result.success(false))
            errorState.eventSink.invoke(SecureBackupRootEvents.RetryKeyBackupState)
            val loadingState2 = awaitItem()
            assertThat(loadingState2.doesBackupExistOnServer).isInstanceOf(AsyncData.Loading::class.java)
            val finalState = awaitItem()
            assertThat(finalState.doesBackupExistOnServer.dataOrNull()).isFalse()
        }
    }

    @Test
    fun `present - setting up encryption when key storage is disabled should emit a state to render a dialog`() = runTest {
        val presenter = createSecureBackupRootPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(2)
            val initialState = awaitItem()
            initialState.eventSink(SecureBackupRootEvents.DisplayKeyStorageDisabledError)
            assertThat(awaitItem().displayKeyStorageDisabledError).isTrue()
            initialState.eventSink(SecureBackupRootEvents.DismissDialog)
            assertThat(awaitItem().displayKeyStorageDisabledError).isFalse()
        }
    }

    @Test
    fun `present - enable key storage invoke the expected API`() = runTest {
        val presenter = createSecureBackupRootPresenter()
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present()
        }.test {
            skipItems(2)
            val initialState = awaitItem()
            initialState.eventSink(SecureBackupRootEvents.EnableKeyStorage)
            assertThat(awaitItem().enableAction.isLoading()).isTrue()
            assertThat(awaitItem().enableAction.isSuccess()).isTrue()
        }
    }

    private fun createSecureBackupRootPresenter(
        encryptionService: EncryptionService = FakeEncryptionService(),
        appName: String = "Zenobia",
    ): SecureBackupRootPresenter {
        return SecureBackupRootPresenter(
            encryptionService = encryptionService,
            buildMeta = aBuildMeta(applicationName = appName),
            snackbarDispatcher = SnackbarDispatcher(),
        )
    }
}
