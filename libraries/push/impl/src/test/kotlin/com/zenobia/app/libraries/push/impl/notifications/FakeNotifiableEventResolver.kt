/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.notifications

import com.zenobia.app.libraries.matrix.api.core.SessionId
import com.zenobia.app.libraries.push.impl.db.PushRequest
import com.zenobia.app.libraries.push.impl.notifications.model.ResolvedPushEvent
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeNotifiableEventResolver(
    private val resolveEventsResult: (SessionId, List<PushRequest>) -> Result<Map<PushRequest, Result<ResolvedPushEvent>>> =
        { _, _ -> lambdaError() }
) : NotifiableEventResolver {
    override suspend fun resolveEvents(
        sessionId: SessionId,
        notificationEventRequests: List<PushRequest>
    ): Result<Map<PushRequest, Result<ResolvedPushEvent>>> {
        return resolveEventsResult(sessionId, notificationEventRequests)
    }
}
