package com.example.plugins.news

import com.example.feauteres.controllers.ChatController
import com.example.feauteres.controllers.MemberAlreadyExistException
import com.example.feauteres.model.ChatReceiveRemote
import com.example.feauteres.model.ChatResponse
import com.example.feauteres.model.MessageReceiveRemote
import com.example.feauteres.model.SessionData
import com.example.plugins.Endpoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import java.util.*

fun Route.chatConfigure(chatController: ChatController) {

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


        webSocket("/auth/chat") {
            println("\n ==== START! \n")

            val chat = call.sessions.get<SessionData>()
            if (chat == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "no session"))
                return@webSocket
            }

            println("\n ==== 1 \n")
            try {
                println("\n ==== 2 \n")
                chatController.onJoinChat(
                    chatID = UUID.fromString(chat.id),
                    sessionID = chat.ownerID,
                    socket = this
                )
                incoming.consumeEach { frame ->
                    if(frame is Frame.Text) {
                        chatController.sendMessageT(
                            ownerID = UUID.fromString(chat.ownerID),
                            companionID = UUID.fromString(chat.companionID),
                            messageJson = frame.readText()
                        )
                    }

                }
            } catch(e: MemberAlreadyExistException) {
                println("\n ==== 3 \n")
                call.respond(HttpStatusCode.Conflict)
            } catch (e: Exception) {
                println("\n ==== 4 \n")
                e.printStackTrace()
            } finally {
                println("\n ==== 5 \n")
                chatController.tryDisconnect(chat.ownerID)
            }
            println("\n ==== 6 \n")
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