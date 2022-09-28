package codes.ka

import codes.ka.processing.runHelpCommand
import codes.ka.processing.runSendCommand
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.helpers.ProcessingScope
import space.jetbrains.api.runtime.types.CommandDetail
import space.jetbrains.api.runtime.types.MessagePayload

data class UserCommand(
    val name: String,
    val args: String?,
    val description: String,
    val example: String?,
    @OptIn(ExperimentalSpaceSdkApi::class) val run: suspend ProcessingScope.(MessagePayload) -> Unit
) {
    fun commandDetail() = CommandDetail(
        name, args?.let { "$it $description" } ?: description
    )
}

@OptIn(ExperimentalSpaceSdkApi::class)
val userCommands = listOf(
    UserCommand("help", null, "show help message", null, ProcessingScope::runHelpCommand),
    UserCommand(
        "send",
        "<channel> <message>",
        "send an anonymous message to the given channel",
        "send #general what does \"ymmv\" stand for?",
        ProcessingScope::runSendCommand
    ),
)