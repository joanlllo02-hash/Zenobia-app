/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.deeplink.impl.usecase

import android.app.Activity
import dev.zacsweers.metro.ContributesBinding
import com.zenobia.app.libraries.androidutils.system.startSharePlainTextIntent
import com.zenobia.app.libraries.core.meta.BuildMeta
import com.zenobia.app.libraries.deeplink.api.usecase.InviteFriendsUseCase
import com.zenobia.app.libraries.di.SessionScope
import com.zenobia.app.libraries.matrix.api.MatrixClient
import com.zenobia.app.libraries.matrix.api.permalink.PermalinkBuilder
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.services.toolbox.api.strings.StringProvider
import timber.log.Timber
import com.zenobia.app.libraries.androidutils.R as AndroidUtilsR

@ContributesBinding(SessionScope::class)
class DefaultInviteFriendsUseCase(
    private val stringProvider: StringProvider,
    private val matrixClient: MatrixClient,
    private val buildMeta: BuildMeta,
    private val permalinkBuilder: PermalinkBuilder,
) : InviteFriendsUseCase {
    override fun execute(activity: Activity) {
        val permalinkResult = permalinkBuilder.permalinkForUser(matrixClient.sessionId)
        permalinkResult.fold(
            onSuccess = { permalink ->
                val appName = buildMeta.applicationName
                activity.startSharePlainTextIntent(
                    activityResultLauncher = null,
                    chooserTitle = stringProvider.getString(CommonStrings.action_invite_friends),
                    text = stringProvider.getString(CommonStrings.invite_friends_text, appName, permalink),
                    extraTitle = stringProvider.getString(CommonStrings.invite_friends_rich_title, appName),
                    noActivityFoundMessage = stringProvider.getString(AndroidUtilsR.string.error_no_compatible_app_found)
                )
            },
            onFailure = {
                Timber.e(it)
            }
        )
    }
}
