/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.mediaviewer.impl.viewer

import com.zenobia.app.libraries.matrix.api.core.EventId
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeMediaViewerNavigator(
    private val onViewInTimelineClickLambda: (EventId) -> Unit = { lambdaError() },
    private val onForwardClickLambda: (EventId, Boolean) -> Unit = { _, _ -> lambdaError() },
    private val onItemDeletedLambda: () -> Unit = { lambdaError() },
) : MediaViewerNavigator {
    override fun onViewInTimelineClick(eventId: EventId) {
        onViewInTimelineClickLambda(eventId)
    }

    override fun onForwardClick(eventId: EventId, fromPinnedEvents: Boolean) {
        onForwardClickLambda(eventId, fromPinnedEvents)
    }

    override fun onItemDeleted() {
        onItemDeletedLambda()
    }
}
