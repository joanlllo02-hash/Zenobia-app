/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.oidc

import com.google.common.truth.Truth.assertThat
import com.zenobia.app.services.toolbox.test.strings.FakeStringProvider
import com.zenobia.app.R
import org.junit.Test

class DefaultOAuthRedirectUrlProviderTest {
    @Test
    fun `test provide`() {
        val stringProvider = FakeStringProvider(
            defaultResult = "str"
        )
        val sut = DefaultOAuthRedirectUrlProvider(
            stringProvider = stringProvider,
        )
        val result = sut.provide()
        assertThat(result).isEqualTo("str:/")
        assertThat(stringProvider.lastResIdParam).isEqualTo(R.string.login_redirect_scheme)
    }
}
