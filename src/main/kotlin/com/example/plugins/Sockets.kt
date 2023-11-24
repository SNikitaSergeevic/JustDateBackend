package com.example.plugins

import com.example.feauteres.model.MessageReceiveRemote
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.webSocket
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
    }
    routing {
        authenticate("auth-jwt") {
            webSocket("/auth/talk") { // websocketSession
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        outgoing.send(Frame.Text("YOU SAID: $text"))
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
                    }
                }
            }

            webSocket("/auth/sendMessage") { // websocketSession

                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        val receiveMessage = Json.decodeFromString<MessageReceiveRemote>(text)
                        outgoing.send(Frame.Text("YOU SAID: $text"))
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
//                        val receiveMessage = receiveDeserialized<MessageReceiveRemote>()
                        println("message: ${receiveMessage.text} \n sender: ${receiveMessage.senderID} \n recipient: ${receiveMessage.recipientID}")
                    }
                }

                val receive = incoming.receive()

            }

            val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
            webSocket("/chat") {
                println("Adding user!")
                val thisConnection = Connection(this)
                connections += thisConnection
                try {
                    send("You are connected! There are ${connections.count()} users here.")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        val textWithUsername = "[${thisConnection.name}]: $receivedText"
                        connections.forEach {
                            it.session.send(textWithUsername)
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    println("Removing $thisConnection!")
                    connections -= thisConnection
                }
            }

        }
    }
}


class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"

}
