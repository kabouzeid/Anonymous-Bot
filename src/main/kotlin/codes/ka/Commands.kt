package codes.ka

import codes.ka.processing.runHelpCommand
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.helpers.ProcessingScope
import space.jetbrains.api.runtime.types.CommandDetail
import space.jetbrains.api.runtime.types.MessagePayload

data class UserCommand(
    val name: String,
    val description: String,
    @OptIn(ExperimentalSpaceSdkApi::class) val run: suspend ProcessingScope.(MessagePayload) -> Unit
) {
    fun commandDetail() = CommandDetail(name, description)
}

@OptIn(ExperimentalSpaceSdkApi::class)
val userCommands = listOf(
    UserCommand("help", "show this help message", ProcessingScope::runHelpCommand),
)