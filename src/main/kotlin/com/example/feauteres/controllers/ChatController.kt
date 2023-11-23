package com.example.feauteres.controllers

import com.example.feauteres.model.*
import java.time.LocalDate
import java.util.*
import io.ktor.server.application.*
import io.ktor.server.request.*

class ChatController() {

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



}