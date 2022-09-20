package codes.ka

import codes.ka.db.saveRefreshTokenData
import codes.ka.processing.runHelpCommand
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.Space
import space.jetbrains.api.runtime.helpers.RequestAdapter
import space.jetbrains.api.runtime.helpers.SpaceHttpResponse
import space.jetbrains.api.runtime.helpers.command
import space.jetbrains.api.runtime.helpers.processPayload
import space.jetbrains.api.runtime.types.*

@OptIn(ExperimentalSpaceSdkApi::class)
fun Application.configureRouting() {
    routing {

        post("api/space") {
            val ktorRequestAdapter = object : RequestAdapter {
                override suspend fun receiveText() =
                    call.receiveText()

                override fun getHeader(headerName: String) =
                    call.request.header(headerName)

                override suspend fun respond(httpStatusCode: Int, body: String) =
                    call.respond(HttpStatusCode.fromValue(httpStatusCode), body)
            }

            Space.processPayload(ktorRequestAdapter, spaceHttpClient, AppInstanceStorage) { payload ->
                when (payload) {
                    is InitPayload -> {
                        SpaceHttpResponse.RespondWithOk
                    }

                    is RefreshTokenPayload -> {
                        saveRefreshTokenData(payload)
                        SpaceHttpResponse.RespondWithOk
                    }

                    is ListCommandsPayload -> {
                        call.respondCommandList()
                        SpaceHttpResponse.AlreadyResponded
                    }

                    is MessagePayload -> {
                        when (payload.command()) {
                            "help" -> runHelpCommand(payload)
                        }
                        SpaceHttpResponse.RespondWithOk
                    }

                    else -> {
                        SpaceHttpResponse.RespondWithOk
                    }
                }
            }
        }
    }
}

suspend fun ApplicationCall.respondCommandList() {
    respondText(
        ObjectMapper().writeValueAsString(
            Commands(
                listOf(
                    CommandDetail(
                        "help",
                        "show this help message"
                    )
                )
            )
        ),
        ContentType.Application.Json,
        HttpStatusCode.OK
    )
}
