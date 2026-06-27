/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.libraries.push.impl.test

import com.zenobia.app.appconfig.PushConfig
import com.zenobia.app.libraries.push.impl.pushgateway.PushGatewayNotifyRequest
import com.zenobia.app.libraries.pushproviders.test.aSessionPushConfig
import com.zenobia.app.tests.testutils.lambda.lambdaRecorder
import com.zenobia.app.tests.testutils.lambda.value
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultTestPushTest {
    @Test
    fun `test DefaultTestPush`() = runTest {
        val executeResult = lambdaRecorder<PushGatewayNotifyRequest.Params, Unit> { }
        val defaultTestPush = DefaultTestPush(
            pushGatewayNotifyRequest = FakePushGatewayNotifyRequest(
                executeResult = executeResult,
            )
        )
        val aConfig = aSessionPushConfig()
        defaultTestPush.execute(aConfig)
        executeResult.assertions()
            .isCalledOnce()
            .with(
                value(
                    PushGatewayNotifyRequest.Params(
                        url = aConfig.url,
                        appId = PushConfig.PUSHER_APP_ID,
                        pushKey = aConfig.pushKey,
                        eventId = DefaultTestPush.TEST_EVENT_ID,
                        roomId = DefaultTestPush.TEST_ROOM_ID,
                    )
                )
            )
    }
}
