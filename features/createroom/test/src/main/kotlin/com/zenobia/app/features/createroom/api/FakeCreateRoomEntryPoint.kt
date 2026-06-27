/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.createroom.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.zenobia.app.libraries.matrix.api.core.RoomId
import com.zenobia.app.tests.testutils.lambda.lambdaError

class FakeCreateRoomEntryPoint : CreateRoomEntryPoint {
    class Builder : CreateRoomEntryPoint.Builder {
        override fun setIsSpace(isSpace: Boolean): Builder = this
        override fun setParentSpace(parentSpaceId: RoomId): Builder = this
        override fun build(): Node = lambdaError()
    }

    override fun builder(
        parentNode: Node,
        buildContext: BuildContext,
        callback: CreateRoomEntryPoint.Callback,
    ): Builder = lambdaError()
}
