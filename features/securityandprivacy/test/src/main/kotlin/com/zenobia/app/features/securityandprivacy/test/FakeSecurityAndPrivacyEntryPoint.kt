/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.securityandprivacy.test

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.zenobia.app.features.securityandprivacy.api.SecurityAndPrivacyEntryPoint
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeSecurityAndPrivacyEntryPoint : SecurityAndPrivacyEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: SecurityAndPrivacyEntryPoint.Callback,
    ): Node {
        lambdaError()
    }
}
