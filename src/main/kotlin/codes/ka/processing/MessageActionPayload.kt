package codes.ka.processing

import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.helpers.ProcessingScope
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.ChannelIdentifier
import space.jetbrains.api.runtime.types.ChatMessageIdentifier
import space.jetbrains.api.runtime.types.MessageActionPayload

@OptIn(ExperimentalSpaceSdkApi::class)
suspend fun ProcessingScope.process(payload: MessageActionPayload) {
    when (payload.actionId) {
        "send.undo" -> {
            val spaceClient = clientWithClientCredentials()
            val ids = payload.actionValue.lines()
            spaceClient.chats.messages.deleteMessage(
                ChannelIdentifier.Id(ids[0]), ChatMessageIdentifier.InternalId(ids[1])
            )
            spaceClient.chats.messages.deleteMessage(
                ChannelIdentifier.Id(payload.message.channelId),
                ChatMessageIdentifier.InternalId(payload.message.messageId)
            )
        }
    }
}