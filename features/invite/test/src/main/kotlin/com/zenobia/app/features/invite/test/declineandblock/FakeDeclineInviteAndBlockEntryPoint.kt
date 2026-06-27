/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.invite.test.declineandblock

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.zenobia.app.features.invite.api.InviteData
import com.zenobia.app.features.invite.api.declineandblock.DeclineInviteAndBlockEntryPoint
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeDeclineInviteAndBlockEntryPoint : DeclineInviteAndBlockEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inviteData: InviteData,
    ): Node {
        lambdaError()
    }
}
