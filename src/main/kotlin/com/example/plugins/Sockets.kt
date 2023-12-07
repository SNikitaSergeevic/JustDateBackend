package com.example.plugins

import com.example.feauteres.controllers.ChatController
import com.example.feauteres.controllers.MemberAlreadyExistException
import com.example.feauteres.model.ChatReceiveRemote
import com.example.feauteres.model.ChatResponse
import com.example.feauteres.model.MessageReceiveRemote
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
}


class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"

}



fun Route.chatSocket(chatController: ChatController) {



}
