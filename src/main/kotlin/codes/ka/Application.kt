package codes.ka

import codes.ka.db.initDbConnection
import io.ktor.server.application.*
import space.jetbrains.api.runtime.ktorClientForSpace

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    initDbConnection()

    configureRouting()
}

val spaceHttpClient = ktorClientForSpace()
