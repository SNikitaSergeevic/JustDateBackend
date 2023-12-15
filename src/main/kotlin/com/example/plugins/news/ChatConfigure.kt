package com.example.plugins.news

import com.example.feauteres.controllers.ChatController
import com.example.feauteres.controllers.MemberAlreadyExistException
import com.example.feauteres.model.ChatModel
import com.example.feauteres.model.ChatResponse
import com.example.feauteres.model.MessageReceiveRemote
import com.example.feauteres.model.SessionData
import com.example.plugins.Endpoint
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.serialization.kotlinx.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.JsonDecoder
import java.util.*

//fun Route.chatConfigure(chatController: ChatController) {
//
//    authenticate("auth-jwt") {
//        webSocket("/auth/talk") { // websocketSession
//
//            println("\n START print for talk ${incoming.receive()} \n")
//            for (frame in incoming) {
//
//                if (frame is Frame.Text) {
//                    val text = frame.readText()
//                    outgoing.send(Frame.Text("YOU SAID: $text"))
//                    if (text.equals("bye", ignoreCase = true)) {
//                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
//                    }
//                }
//            }
//        }
//
//        webSocket("/auth/chat/{ownerID}/{companionID}") {
//            println("\n ==== START! \n")
//
////            val chat = call.sessions.get<SessionData>()
////            val startFrame = Frame.toString()
////            val chat = Json.decodeFromString<SessionData>(startFrame)
//            val ownerID = call.parameters["ownerID"] ?: "null"
//            val companionID = call.parameters["companionID"] ?: "null"
//
//
//            if (ownerID == "null") {
//                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "no session"))
//                return@webSocket
//            }
//
//            println("\n ==== 1 \n")
//            val chat = ChatModel.fetch(UUID.fromString(ownerID), UUID.fromString(companionID))
//
//        }
//
//        get(Endpoint.GetChat.str) {
//            val ownerID = call.parameters["ownerID"]
//            val companionID = call.parameters["companionID"]
//
//            try {
//                val chat = chatController.getChat(UUID.fromString(ownerID), UUID.fromString(companionID))
//                if (chat != null) {
//                    call.respond(
//                        HttpStatusCode.Accepted,
//                        ChatResponse(
//                            id = chat.id.toString(),
//                            ownerID = chat.ownerID.toString(),
//                            companionID = chat.companionID.toString()
//                        )
//                    )
//                }
//            } catch (e: Exception) {
//                call.respond(HttpStatusCode.Conflict, e)
//            }
//
//
//        }
//
//        }
//    }


fun Application.chatConfigure(chatController: ChatController) {

    routing {
        authenticate("auth-jwt") {
            webSocket("/auth/talk") { // websocketSession

                println("\n START print for talk ${incoming.receive()} \n")

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

            webSocket(Endpoint.ChatConnect.str) {
                println("\n ChatConnect START! \n")
                val myID = call.parameters["myID"]
                val companionID = call.parameters["companionID"]

                //todo: Check existing chat for two users
                val companionSessionID = chatController.getChat(UUID.fromString(companionID), UUID.fromString(myID))
                val meSessionID = chatController.getChat(UUID.fromString(myID), UUID.fromString(companionID))

                if (companionSessionID == null || meSessionID == null) {
                    this.closeReason
                }

                val miConnect = chatController.createConnection(myID, companionID, this)

                try {
                    while (true) {
                        when (val frame = incoming.receive()) {
                            is Frame.Text -> {
                                val message = Json.decodeFromString<MessageReceiveRemote>(frame.readText())
                                chatController.sendMessage(message, meSessionID.toString(), companionSessionID.toString())
                            }
                            else -> TODO()
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    chatController.removeConnection(myID, companionID)
                }
            }

            get(Endpoint.GetChat.str) {
                val ownerID = call.parameters["ownerID"]
                val companionID = call.parameters["companionID"]

                try {
                    val chat = chatController.getChat(UUID.fromString(ownerID), UUID.fromString(companionID))
                    if (chat != null) {
                        call.respond(
                            HttpStatusCode.Accepted,
                            ChatResponse(
                                id = chat.id.toString(),
                                ownerID = chat.ownerID.toString(),
                                companionID = chat.companionID.toString()
                            )
                        )
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, e)
                }


            }

        }
    }
}


