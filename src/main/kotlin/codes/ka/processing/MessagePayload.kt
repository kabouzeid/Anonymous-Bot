package codes.ka.processing

import codes.ka.channelPermissions
import codes.ka.userCommands
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.PermissionDeniedException
import space.jetbrains.api.runtime.helpers.ProcessingScope
import space.jetbrains.api.runtime.helpers.command
import space.jetbrains.api.runtime.helpers.commandArguments
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.resources.richText
import space.jetbrains.api.runtime.types.*

@OptIn(ExperimentalSpaceSdkApi::class)
suspend fun ProcessingScope.process(payload: MessagePayload) {
    val command = payload.command()
    val userCommand = userCommands.firstOrNull { it.name == command }
    userCommand?.run?.invoke(this, payload)
}

@OptIn(ExperimentalSpaceSdkApi::class)
suspend fun ProcessingScope.runHelpCommand(payload: MessagePayload) {
    clientWithClientCredentials().chats.messages.sendMessage(ChannelIdentifier.Id(payload.message.channelId), message {
        MessageOutline(
            icon = ApiIcon("checkbox-checked"), text = "Anonymous Bot help"
        )
        section {
            text("List of available commands")
            fields {
                userCommands.forEach {
                    var name = it.name
                    name = it.args?.let { args -> "$name $args" } ?: name

                    var description = it.description
                    description = it.example?.let { example -> "$description\n\n*Example:*\n>$example" } ?: description

                    field("`$name`", description)
                }
            }
        }
    })
}

@OptIn(ExperimentalSpaceSdkApi::class)
suspend fun ProcessingScope.runSendCommand(payload: MessagePayload) {
    val spaceClient = clientWithClientCredentials()

    class NoChannelException : RuntimeException()
    try {
        val args = payload.commandArguments() ?: throw NoChannelException()
        val linkNode =
            ((spaceClient.richText.parseMarkdown(args).children.firstOrNull() as? RtParagraph)?.children?.firstOrNull() as? RtText)
                ?: throw NoChannelException()
        val value = linkNode.value
        val href = linkNode.marks.firstNotNullOfOrNull { it as? RtLinkMark }?.attrs?.href ?: throw NoChannelException()

        // this would be much simpler if we would know the range of the detected link. here we try to find it manually
        val text = listOf("[\\$value]($href)", "[$value]($href)", value, "\\$value").filter { args.length >= it.length }
            .firstOrNull {
                it.regionMatches(0, args, 0, it.length)
            }?.let {
                args.substring(it.length).trimStart()
            } ?: args

        println("args=$args\nvalue=${linkNode.value}\nhref=$href")

        val channelId = with(href) {
            val index = lastIndexOf("/")
            if (index < 0 || index >= href.length - 1) throw NoChannelException()
            substring(index + 1)
        }

        try {
            val sentMessageRecord = spaceClient.chats.messages.sendMessage(
                ChannelIdentifier.Id(channelId),
                message { section { text(text) } }) { id() }
            spaceClient.chats.messages.sendMessage(ChannelIdentifier.Id(payload.message.channelId), message {
                section {
                    text("Your message has been sent 🎉")
                    controls {
                        button(
                            "Undo",
                            PostMessageAction("send.undo", "$channelId\n${sentMessageRecord.id}"),
                            MessageButtonStyle.DANGER
                        )
                    }
                }
            })
        } catch (e: PermissionDeniedException) {
            spaceClient.chats.messages.sendMessage(ChannelIdentifier.Id(payload.message.channelId), message {
                section {
                    text(
                        "Sorry, I'm not authorized to send messages into this channel 😔 Ask your system administrator or the channel administrator to grant me permission to send messages into this channel.",
                        MessageStyle.ERROR
                    )
                }
            })
            spaceClient.applications.authorizations.authorizedRights.requestRights(
                ApplicationIdentifier.Me, ChannelPermissionContextIdentifier(channelId), channelPermissions
            )
            return
        }
    } catch (e: NoChannelException) {
        spaceClient.chats.messages.sendMessage(ChannelIdentifier.Id(payload.message.channelId), message {
            section {
                text(
                    "Please provide a valid channel name. Use `help` for instructions.", MessageStyle.ERROR
                )
            }
        })
        return
    }
}
