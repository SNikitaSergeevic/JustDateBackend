package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import java.time.LocalDate
import java.util.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.util.concurrent.ConcurrentHashMap




class MemberAlreadyExistException: Exception ("There is already a member with that chatID in the room.")

class Connection(s: DefaultWebSocketSession, id: UUID) {
    val session = s
    val id: UUID = id
}

class ChatController() {


    private val members = ConcurrentHashMap <String, ChatSocketSession>()
    private val client = HttpClient(CIO)
    private var connections = Collections.synchronizedMap<String, Connection>(LinkedHashMap())

    // TODO: WS controls
    fun createConnection(idOne: String?, idTwo: String?, session: DefaultWebSocketSession): Connection? {
        val chat = getChat(UUID.fromString(idOne), UUID.fromString(idTwo))

        return if (chat != null) {
            val newConnection = Connection(session, chat.id)
            connections[chat.id.toString()] = newConnection
            newConnection
        } else {
            null
        }
    }

    fun removeConnection(idOne: String?, idTwo: String?) {
        val chat = getChat(UUID.fromString(idOne), UUID.fromString(idTwo))
        connections.remove(chat!!.id.toString())
    }

    fun getChat(ownerID: UUID, companionID: UUID): ChatDTO? {
        return ChatModel.fetch(ownerID, companionID)
    }

    // TODO: Message controls
    suspend fun sendMessage(message: MessageReceiveRemote, oneSessionID: String?, twoSessionID: String?) {
        println("\n ${message.text} \n")

        val now = java.util.Date().time

        //todo: check and get connections
        val s1 = connections[oneSessionID]
        val s2 = connections[twoSessionID]

        println("\n ${message.chatID} ${message.senderID} ${message.recipientID} ${message.text}\n")
        println("\n oneSessionID - $oneSessionID , twoSessionID - $twoSessionID ")

        //todo: save messages in db, and convert for respond
        val messageDTOOneSession = MessageDTO (
            id = UUID.randomUUID(),
            chatID = UUID.fromString(oneSessionID),
            senderID = UUID.fromString(message.senderID),
            recipientID = UUID.fromString(message.recipientID),
            text = message.text,
            createdAt = now
        )
        MessageModel.create(messageDTOOneSession)
        val messageForS1 = MessageResponseRemote(
            id = messageDTOOneSession.id.toString(),
            chatID = messageDTOOneSession.chatID.toString(),
            senderID = messageDTOOneSession.senderID.toString(),
            recipientID = messageDTOOneSession.recipientID.toString(),
            text = message.text,
            createdAt = now
        )

        val messageDTOTwoSession = MessageDTO (
            id = UUID.randomUUID(),
            chatID = UUID.fromString(twoSessionID),
            senderID = UUID.fromString(message.senderID),
            recipientID = UUID.fromString(message.recipientID),
            text = message.text,
            createdAt = now
        )
        MessageModel.create(messageDTOTwoSession)
        val messageForS2 = MessageResponseRemote(
            id = messageDTOTwoSession.id.toString(),
            chatID = messageDTOTwoSession.chatID.toString(),
            senderID = messageDTOTwoSession.senderID.toString(),
            recipientID = messageDTOTwoSession.recipientID.toString(),
            text = message.text,
            createdAt = now
        )

        if (s1 != null) {
//            s1.session.send(message.toString())
            s1.session.send(Json.encodeToJsonElement(messageForS1).toString())
        } else {
            println("ChatController fun sendMessage, s1 == null")
            //FIXME append notification
        }

        if (s2 != null) {
//            s2.session.send(message.toString())
            s2.session.send(Json.encodeToJsonElement(messageForS2).toString())
        } else {
            println("ChatController fun sendMessage, s2 == null")
            //FIXME append notification
        }

    }



    //FIXME down is not use
    fun getConnection(idOne: String?, idTwo: String?): Connection? {
        val chat = getChat(UUID.fromString(idOne), UUID.fromString(idTwo))
        return connections[chat!!.id.toString()]
    }

    fun onJoinChat(ownerID: String, companionID: String, socket: WebSocketSession) {
//            sessionID this chat.ownerID

        val chat = ChatModel.fetch(UUID.fromString(ownerID), UUID.fromString(companionID))

        if (chat != null) {
            if (members.containsKey(chat.ownerID.toString())) {
                throw MemberAlreadyExistException()
            }

            val chatSocketSession = ChatSocketSession(
                id = UUID.randomUUID(),
                chatDTO = chat,
                socketSession = socket
            )

            members[ownerID] = chatSocketSession
        }

    }

    suspend fun getChats() {
        client.webSocket(method = HttpMethod.Get, host = "0.0.0.0", port = 8443, path = "/check/work") {
            val chat = receiveDeserialized<ChatResponse>()
            println("\n receive = $chat \n")
        }
    }

    suspend fun sendMessageT(ownerID: UUID, companionID: UUID, messageJson: String) {
        val messageReceiveRemote = Json.decodeFromString<MessageReceiveRemote>(messageJson)

        val currentDate = java.util.Date().time
        val chatID = UUID.fromString(messageReceiveRemote.chatID)
        val senderID = UUID.fromString(messageReceiveRemote.senderID)
        val recipientID = UUID.fromString(messageReceiveRemote.recipientID)

        val ownerSession = members[ownerID.toString()]
        val companionSession = members[companionID.toString()]

        val messageDTOSenderChat = MessageDTO(
            id = UUID.randomUUID(),
            chatID = chatID,
            senderID = senderID,
            recipientID = recipientID,
            text = messageReceiveRemote.text,
            createdAt = currentDate
        )
        MessageModel.create(messageDTOSenderChat)

        val recipientChat = ChatModel.fetch(
            ownerID = recipientID,
            companionID = senderID
        )

        if (recipientChat != null) {
            val messageDTORecipientChat = MessageDTO(
                id = UUID.randomUUID(),
                chatID = recipientChat.id,
                senderID = senderID,
                recipientID = recipientID,
                text = messageReceiveRemote.text,
                createdAt = currentDate
            )
            MessageModel.create(messageDTORecipientChat)
        }

        if (ownerSession != null) {
            ownerSession.socketSession.send(content = messageJson)
        }

        if (companionSession != null) {
            companionSession.socketSession.send(content = messageJson)
        }

        // this send notification message

    }

    suspend fun tryDisconnect(ownerID: String) {
        members[ownerID]?.socketSession?.close()
        if (members.containsKey(ownerID)) {
            members.remove(ownerID)
        }
    }

    fun createChat(ownerID: UUID, companionID: UUID): ChatDTO? {
        val date = java.util.Date().time
        val ownerChatDTO = ChatDTO(
            id = UUID.randomUUID(),
            ownerID = ownerID,
            companionID = companionID,
            createdAt = date
        )

        val companionChatDTO = ChatDTO(
            id = UUID.randomUUID(),
            ownerID = companionID,
            companionID = ownerID,
            createdAt = date
        )

        ChatModel.create(ownerChatDTO)
        ChatModel.create(companionChatDTO)
        return ownerChatDTO
    }

    fun sendMessage(ownerID: UUID, companionID: UUID, text: String): MessageDTO? {
        val date = LocalDate.now()
        val ownerChat = ChatModel.fetch(ownerID, companionID)
        val companionChat = ChatModel.fetch(companionID, ownerID)

        return if (ownerChat != null && companionChat != null) {

            val ownerMessageDTO = MessageDTO(
                id = UUID.randomUUID(),
                chatID = ownerChat.id,
                senderID = ownerID,
                recipientID = companionID,
                text = text,
                createdAt = ownerChat.createdAt
            )

            val companionMessageDTO = MessageDTO(
                id = UUID.randomUUID(),
                chatID = companionChat.id,
                senderID = ownerID,
                recipientID = companionID,
                text = text,
                createdAt = companionChat.createdAt
            )
            MessageModel.create(ownerMessageDTO)
            MessageModel.create(companionMessageDTO)

            ownerMessageDTO
        } else {
            null
        }

    }



    fun getAllMessageForChat(chatID: UUID): List<MessageDTO>? {
        return MessageModel.fetchMessage(chatID)
    }








}