/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalTestApi::class)

package com.zenobia.app.libraries.mediaviewer.impl.details

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.libraries.ui.strings.CommonStrings
import com.zenobia.app.tests.testutils.EnsureNeverCalled
import com.zenobia.app.tests.testutils.EnsureNeverCalledWithParam
import com.zenobia.app.tests.testutils.clickOn
import com.zenobia.app.tests.testutils.ensureCalledOnce
import com.zenobia.app.tests.testutils.ensureCalledOnceWithParam
import com.zenobia.app.tests.testutils.robolectric.RobolectricTest
import com.zenobia.app.tests.testutils.setSafeContent
import org.junit.Test

class MediaDeleteConfirmationBottomSheetTest : RobolectricTest() {
    @Test
    fun `clicking on Cancel invokes expected callback`() = runAndroidComposeUiTest {
        val state = aMediaBottomSheetStateDeleteConfirmation()
        ensureCalledOnce { callback ->
            setMediaDeleteConfirmationBottomSheet(
                state = state,
                onDismiss = callback,
            )
            clickOn(CommonStrings.action_cancel)
        }
    }

    @Test
    fun `clicking on Remove invokes expected callback`() = runAndroidComposeUiTest {
        val state = aMediaBottomSheetStateDeleteConfirmation()
        ensureCalledOnceWithParam(state.eventId) { callback ->
            setMediaDeleteConfirmationBottomSheet(
                state = state,
                onDelete = callback,
            )
            onNodeWithText(activity!!.getString(CommonStrings.action_remove)).assertExists()
            clickOn(CommonStrings.action_remove)
        }
    }
}

private fun AndroidComposeUiTest<ComponentActivity>.setMediaDeleteConfirmationBottomSheet(
    state: MediaBottomSheetState.DeleteConfirmation,
    onDelete: (EventId) -> Unit = EnsureNeverCalledWithParam(),
    onDismiss: () -> Unit = EnsureNeverCalled(),
) {
    setSafeContent {
        MediaDeleteConfirmationBottomSheet(
            state = state,
            onDelete = onDelete,
            onDismiss = onDismiss,
        )
    }
}
