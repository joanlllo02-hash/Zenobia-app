/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(DelicateCoilApi::class)

package com.zenobia.app.appnav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import com.zenobia.app.annotations.ContributesNode
import com.zenobia.app.features.login.api.LoginEntryPoint
import com.zenobia.app.features.login.api.LoginParams
import com.zenobia.app.libraries.architecture.BackstackView
import com.zenobia.app.libraries.architecture.BaseFlowNode
import com.zenobia.app.libraries.architecture.NodeInputs
import com.zenobia.app.libraries.architecture.callback
import com.zenobia.app.libraries.architecture.inputs
import com.zenobia.app.libraries.designsystem.utils.ForceOrientationInMobileDevices
import com.zenobia.app.libraries.designsystem.utils.ScreenOrientation
import com.zenobia.app.libraries.matrix.ui.media.ImageLoaderHolder
import com.zenobia.app.services.analytics.api.watchers.AnalyticsColdStartWatcher
import kotlinx.parcelize.Parcelize

@ContributesNode(AppScope::class)
@AssistedInject
class NotLoggedInFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val loginEntryPoint: LoginEntryPoint,
    private val imageLoaderHolder: ImageLoaderHolder,
    private val analyticsColdStartWatcher: AnalyticsColdStartWatcher,
) : BaseFlowNode<NotLoggedInFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    data class Params(
        val loginParams: LoginParams?,
    ) : NodeInputs

    interface Callback : Plugin {
        fun navigateToBugReport()
        fun onDone()
    }

    private val callback: Callback = callback()
    private val inputs = inputs<Params>()

    override fun onBuilt() {
        super.onBuilt()
        analyticsColdStartWatcher.whenLoggingIn()
        lifecycle.subscribe(
            onResume = {
                SingletonImageLoader.setUnsafe(imageLoaderHolder.get())
            },
        )
    }

    sealed interface NavTarget : Parcelable {
        @Parcelize
        data object Root : NavTarget
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Root -> {
                val callback = object : LoginEntryPoint.Callback {
                    override fun navigateToBugReport() {
                        callback.navigateToBugReport()
                    }

                    override fun onDone() {
                        callback.onDone()
                    }
                }
                loginEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = LoginEntryPoint.Params(
                        accountProvider = inputs.loginParams?.accountProvider,
                        loginHint = inputs.loginParams?.loginHint,
                    ),
                    callback = callback,
                )
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        // The login flow doesn't support landscape mode on mobile devices yet
        ForceOrientationInMobileDevices(orientation = ScreenOrientation.PORTRAIT)

        BackstackView()
    }
}
