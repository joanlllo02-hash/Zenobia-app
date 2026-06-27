/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.changeroommemberroles.test

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.zenobia.app.features.rolesandpermissions.api.RolesAndPermissionsEntryPoint
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeRolesAndPermissionsEntryPoint : RolesAndPermissionsEntryPoint {
    override fun createNode(parentNode: Node, buildContext: BuildContext, callback: RolesAndPermissionsEntryPoint.Callback): Node {
        lambdaError()
    }
}
