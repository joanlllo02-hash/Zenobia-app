/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.preferences.impl.root

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.services.toolbox.api.strings.StringProvider

interface VersionFormatter {
    fun get(): String
}

@ContributesBinding(AppScope::class)
class DefaultVersionFormatter(
    private val stringProvider: StringProvider,
    private val buildMeta: BuildMeta,
) : VersionFormatter {
    override fun get(): String {
        val base = stringProvider.getString(
            CommonStrings.settings_version_number,
            buildMeta.versionName,
            buildMeta.versionCode.toString()
        )
        return if (buildMeta.gitBranchName == "main") {
            base
        } else {
            // In case of a build not from main, we display the branch name and the revision
            "$base\n${buildMeta.gitBranchName} (${buildMeta.gitRevision})"
        }
    }
}
