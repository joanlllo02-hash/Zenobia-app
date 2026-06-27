/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.matrix.impl.storage

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.matrix.api.paths.SessionPaths

interface SqliteStoreBuilderProvider {
    fun provide(sessionPaths: SessionPaths): SqliteStoreBuilder
}

@ContributesBinding(AppScope::class)
class RustSqliteStoreBuilderProvider : SqliteStoreBuilderProvider {
    override fun provide(sessionPaths: SessionPaths): SqliteStoreBuilder {
        return RustSqliteStoreBuilder(sessionPaths)
    }
}
