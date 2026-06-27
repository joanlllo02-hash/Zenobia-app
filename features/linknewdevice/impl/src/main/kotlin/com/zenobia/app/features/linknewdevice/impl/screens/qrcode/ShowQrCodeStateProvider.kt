/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.linknewdevice.impl.screens.qrcode

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.libraries.architecture.AsyncData

class ShowQrCodeStateProvider : PreviewParameterProvider<ShowQrCodeState> {
    override val values: Sequence<ShowQrCodeState>
        get() = sequenceOf(
            aShowQrCodeState(),
            aShowQrCodeState(
                data = AsyncData.Loading(),
            ),
        )
}

internal fun aShowQrCodeState(
    data: AsyncData<String> = AsyncData.Success("DATA"),
) = ShowQrCodeState(
    data = data,
)
