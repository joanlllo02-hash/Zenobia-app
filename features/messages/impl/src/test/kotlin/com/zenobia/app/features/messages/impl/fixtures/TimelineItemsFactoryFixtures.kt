/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package com.zenobia.app.features.messages.impl.fixtures

import com.zenobia.app.features.messages.impl.messagesummary.FakeMessageSummaryFormatter
import com.zenobia.app.features.messages.impl.timeline.factories.TimelineItemsFactory
import com.zenobia.app.features.messages.impl.timeline.factories.TimelineItemsFactoryConfig
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentFailedToParseMessageFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentFailedToParseStateFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentMessageFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentPollFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentProfileChangeFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentRedactedFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentRoomMembershipFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentStateFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentStickerFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemContentUTDFactory
import com.zenobia.app.features.messages.impl.timeline.factories.event.TimelineItemEventFactory
import com.zenobia.app.features.messages.impl.timeline.factories.virtual.TimelineItemDaySeparatorFactory
import com.zenobia.app.features.messages.impl.timeline.factories.virtual.TimelineItemVirtualFactory
import com.zenobia.app.features.messages.impl.timeline.groups.TimelineItemGrouper
import com.zenobia.app.features.messages.impl.utils.FakeTextPillificationHelper
import com.zenobia.app.features.messages.test.timeline.FakeHtmlConverterProvider
import com.zenobia.app.features.poll.test.pollcontent.FakePollContentStateFactory
import com.zenobia.app.libraries.androidutils.filesize.FakeFileSizeFormatter
import com.zenobia.app.libraries.dateformatter.test.FakeDateFormatter
import com.zenobia.app.libraries.eventformatter.api.TimelineEventFormatter
import com.zenobia.app.libraries.matrix.api.core.UserId
import com.zenobia.app.libraries.matrix.api.timeline.item.event.EventContent
import com.zenobia.app.libraries.matrix.test.FakeMatrixClient
import com.zenobia.app.libraries.matrix.test.permalink.FakePermalinkParser
import com.zenobia.app.libraries.mediaviewer.test.util.FileExtensionExtractorWithoutValidation
import com.zenobia.app.services.toolbox.test.strings.FakeStringProvider
import com.zenobia.app.tests.testutils.testCoroutineDispatchers
import kotlinx.coroutines.test.TestScope

internal fun TestScope.aTimelineItemsFactoryCreator(): TimelineItemsFactory.Creator {
    return object : TimelineItemsFactory.Creator {
        override fun create(config: TimelineItemsFactoryConfig): TimelineItemsFactory {
            return aTimelineItemsFactory(config)
        }
    }
}

internal fun aTimelineItemContentFactory(
    timelineEventFormatter: TimelineEventFormatter = aTimelineEventFormatter(),
    matrixClient: FakeMatrixClient = FakeMatrixClient(),
): TimelineItemContentFactory = TimelineItemContentFactory(
    messageFactory = TimelineItemContentMessageFactory(
        fileSizeFormatter = FakeFileSizeFormatter(),
        fileExtensionExtractor = FileExtensionExtractorWithoutValidation(),
        htmlConverterProvider = FakeHtmlConverterProvider(),
        permalinkParser = FakePermalinkParser(),
        textPillificationHelper = FakeTextPillificationHelper(),
    ),
    redactedMessageFactory = TimelineItemContentRedactedFactory(),
    stickerFactory = TimelineItemContentStickerFactory(
        fileSizeFormatter = FakeFileSizeFormatter(),
        fileExtensionExtractor = FileExtensionExtractorWithoutValidation()
    ),
    pollFactory = TimelineItemContentPollFactory(FakePollContentStateFactory()),
    utdFactory = TimelineItemContentUTDFactory(),
    roomMembershipFactory = TimelineItemContentRoomMembershipFactory(timelineEventFormatter),
    profileChangeFactory = TimelineItemContentProfileChangeFactory(timelineEventFormatter),
    stateFactory = TimelineItemContentStateFactory(timelineEventFormatter),
    failedToParseMessageFactory = TimelineItemContentFailedToParseMessageFactory(),
    failedToParseStateFactory = TimelineItemContentFailedToParseStateFactory(),
    sessionId = matrixClient.sessionId,
    dateFormatter = FakeDateFormatter(),
    stringProvider = FakeStringProvider(),
)

internal fun TestScope.aTimelineItemsFactory(
    config: TimelineItemsFactoryConfig,
): TimelineItemsFactory {
    val matrixClient = FakeMatrixClient()
    return TimelineItemsFactory(
        dispatchers = testCoroutineDispatchers(),
        eventItemFactoryCreator = object : TimelineItemEventFactory.Creator {
            override fun create(config: TimelineItemsFactoryConfig): TimelineItemEventFactory {
                return TimelineItemEventFactory(
                    contentFactory = aTimelineItemContentFactory(matrixClient = matrixClient),
                    matrixClient = matrixClient,
                    dateFormatter = FakeDateFormatter(),
                    permalinkParser = FakePermalinkParser(),
                    config = config,
                    summaryFormatter = FakeMessageSummaryFormatter(),
                )
            }
        },
        virtualItemFactory = TimelineItemVirtualFactory(
            daySeparatorFactory = TimelineItemDaySeparatorFactory(
                FakeDateFormatter()
            ),
        ),
        timelineItemGrouper = TimelineItemGrouper(),
        config = config
    )
}

internal fun aTimelineEventFormatter(): TimelineEventFormatter {
    return object : TimelineEventFormatter {
        override fun format(content: EventContent, isOutgoing: Boolean, sender: UserId, senderDisambiguatedDisplayName: String): CharSequence? {
            return ""
        }
    }
}
