package codes.ka.processing

import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.helpers.ProcessingScope
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.ChannelIdentifier
import space.jetbrains.api.runtime.types.ChatMessage
import space.jetbrains.api.runtime.types.MessagePayload

@OptIn(ExperimentalSpaceSdkApi::class)
suspend fun ProcessingScope.runHelpCommand(payload: MessagePayload) {
    val spaceClient = clientWithClientCredentials()
    spaceClient.chats.messages.sendMessage(ChannelIdentifier.Id(payload.message.channelId), helpMessage())
}

fun helpMessage(): ChatMessage {
    return message {
        section {
            text("Soon the help will be shown here!")
        }
    }
}