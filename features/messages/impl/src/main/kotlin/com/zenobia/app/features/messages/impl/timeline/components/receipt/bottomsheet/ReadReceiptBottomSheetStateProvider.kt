/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.timeline.components.receipt.bottomsheet

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zenobia.app.features.messages.impl.timeline.aTimelineItemEvent
import com.zenobia.app.features.messages.impl.timeline.components.receipt.ReadReceiptViewStateProvider
import com.zenobia.app.features.messages.impl.timeline.model.TimelineItemReadReceipts
import com.zenobia.app.libraries.matrix.api.timeline.item.event.LocalEventSendState
import kotlinx.collections.immutable.toImmutableList

class ReadReceiptBottomSheetStateProvider : PreviewParameterProvider<ReadReceiptBottomSheetState> {
    // Reuse the provider ReadReceiptViewStateProvider
    private val readReceiptViewStateProvider = ReadReceiptViewStateProvider()
    override val values: Sequence<ReadReceiptBottomSheetState> = readReceiptViewStateProvider.values
        .filter { it.sendState is LocalEventSendState.Sent }
        .map { readReceiptViewState ->
            ReadReceiptBottomSheetState(
                selectedEvent = aTimelineItemEvent(
                    readReceiptState = TimelineItemReadReceipts(
                        receipts = readReceiptViewState.receipts.map { readReceiptData ->
                            readReceiptData
                                .copy(avatarData = readReceiptData.avatarData.copy(id = "@${readReceiptData.avatarData.id}:localhost"))
                        }.toImmutableList()
                    )
                ),
                eventSink = {},
            )
        }
}
