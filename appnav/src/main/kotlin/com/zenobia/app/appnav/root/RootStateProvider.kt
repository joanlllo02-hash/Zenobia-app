/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.appnav.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.rageshake.api.crash.aCrashDetectionState
import com.zenobia.app.features.rageshake.api.detection.aRageshakeDetectionState
import com.zenobia.app.services.apperror.api.AppErrorState
import com.zenobia.app.services.apperror.api.aAppErrorState

open class RootStateProvider : PreviewParameterProvider<RootState> {
    override val values: Sequence<RootState>
        get() = sequenceOf(
            aRootState().copy(
                rageshakeDetectionState = aRageshakeDetectionState().copy(showDialog = false),
                crashDetectionState = aCrashDetectionState().copy(crashDetected = true),
            ),
            aRootState().copy(
                rageshakeDetectionState = aRageshakeDetectionState().copy(showDialog = true),
                crashDetectionState = aCrashDetectionState().copy(crashDetected = false),
            ),
            aRootState().copy(
                errorState = aAppErrorState(),
            )
        )
}

fun aRootState() = RootState(
    rageshakeDetectionState = aRageshakeDetectionState(),
    crashDetectionState = aCrashDetectionState(),
    errorState = AppErrorState.NoError,
)
