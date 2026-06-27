/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.lockscreen.impl.fixtures

import com.zenobia.app.features.lockscreen.impl.pin.DefaultPinCodeManager
import com.zenobia.app.features.lockscreen.impl.pin.PinCodeManager
import com.zenobia.app.features.lockscreen.impl.pin.storage.InMemoryLockScreenStore
import com.zenobia.app.features.lockscreen.impl.storage.LockScreenStore
import com.zenobia.app.libraries.cryptography.api.EncryptionDecryptionService
import com.zenobia.app.libraries.cryptography.impl.AESEncryptionDecryptionService
import com.zenobia.app.libraries.cryptography.test.SimpleSecretKeyRepository

internal fun aPinCodeManager(
    lockScreenStore: LockScreenStore = InMemoryLockScreenStore(),
    secretKeyRepository: SimpleSecretKeyRepository = SimpleSecretKeyRepository(),
    encryptionDecryptionService: EncryptionDecryptionService = AESEncryptionDecryptionService(),
): PinCodeManager {
    return DefaultPinCodeManager(secretKeyRepository, encryptionDecryptionService, lockScreenStore)
}
