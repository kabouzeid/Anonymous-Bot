package codes.ka.processing

import codes.ka.userCommands
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.helpers.ProcessingScope
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.*

@OptIn(ExperimentalSpaceSdkApi::class)
suspend fun ProcessingScope.runHelpCommand(payload: MessagePayload) {
    val spaceClient = clientWithClientCredentials()
    spaceClient.chats.messages.sendMessage(ChannelIdentifier.Id(payload.message.channelId), helpMessage())
}

fun helpMessage(): ChatMessage {
    return message {
        MessageOutline(
            icon = ApiIcon("checkbox-checked"),
            text = "Random Coffee bot help"
        )
        section {
            text("List of available commands", MessageStyle.PRIMARY)
            fields {
                userCommands.forEach {
                    field(it.name, it.description)
                }
            }
        }
    }
}