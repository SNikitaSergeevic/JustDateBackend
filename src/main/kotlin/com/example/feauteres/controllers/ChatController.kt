package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.serialization.*
import java.time.LocalDate
import java.util.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap




class MemberAlreadyExistException: Exception ("There is already a member with that chatID in the room.")



class ChatController() {


    private val members = ConcurrentHashMap <String, ChatSocketSession>()

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

    suspend fun sendMessageT(ownerID: UUID, companionID: UUID, messageJson: String) {
        val messageReceiveRemote = Json.decodeFromString<MessageReceiveRemote>(messageJson)

        val currentDate = LocalDate.now()
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
        val date = LocalDate.now()
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

    fun getChat(ownerID: UUID, companionID: UUID): ChatDTO? {
        return ChatModel.fetch(ownerID, companionID)
    }






}