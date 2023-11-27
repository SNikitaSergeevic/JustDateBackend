package com.example.feauteres.model

import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.UUID


//TODO: ChatModel

data class SessionData(
    val ownerID: String,
    val companionID: String
)

@Serializable
data class ChatReceiveRemote(
    val id: String,
    val ownerID: String,
    val companionID: String
)

@Serializable
data class ChatResponse(
    val id: String,
    val ownerID: String,
    val companionID: String
)

class ChatSocketSession(
    val id: UUID,
    val chatDTO: ChatDTO,
    val socketSession: WebSocketSession
)

class ChatDTO (val id: UUID,
               val ownerID: UUID,
               val companionID: UUID,
               val createdAt: LocalDate)

object ChatModel: Table("chat")  { // chat not exist in DB
    private val id: Column<UUID> = ChatModel.uuid("id")
    private val ownerID: Column<UUID> = ChatModel.uuid("owner_id")
    private val companionID: Column<UUID> = ChatModel.uuid("companion_id")
    private val createdAt: Column<LocalDate> = ChatModel.date("created_at")

    fun create(chatDTO: ChatDTO) {
        transaction {
            ChatModel.insert {
                it[id] = chatDTO.id
                it[ownerID] = chatDTO.ownerID
                it[companionID] = chatDTO.companionID
                it[createdAt] = chatDTO.createdAt
            }
        }
    }

    fun fetch(ownerID: UUID, companionID: UUID): ChatDTO? {
        return try {
            transaction {
                val chat = ChatModel.select {(ChatModel.ownerID eq ownerID) and (ChatModel.companionID eq companionID)}.single()
                ChatDTO(
                    id = chat[ChatModel.id],
                    ownerID = chat[ChatModel.ownerID],
                    companionID = chat[ChatModel.companionID],
                    createdAt = chat[ChatModel.createdAt]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchOnChatID(chatID: UUID): ChatDTO? {
        return try {
            transaction {
                val chat = ChatModel.select{ChatModel.id eq chatID}.single()
                ChatDTO(
                    id = chat[ChatModel.id],
                    ownerID = chat[ChatModel.ownerID],
                    companionID = chat[ChatModel.companionID],
                    createdAt = chat[ChatModel.createdAt]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

}



//TODO: MessageModel

@Serializable
data class MessageReceiveRemote(
    val chatID: String,
    val senderID: String,
    val recipientID: String,
    val text: String
)

class MessageDTO (val id: UUID,
                  val chatID: UUID,
                  val senderID: UUID,
                  val recipientID: UUID,
                  val text: String,
                  val createdAt: LocalDate)

object MessageModel: Table("message") {
    private val id: Column<UUID> = MessageModel.uuid("id")
    private val chatID: Column<UUID> = MessageModel.uuid("chat_id")
    private val senderID: Column<UUID> = MessageModel.uuid("sender_id")
    private val recipientID: Column<UUID> = MessageModel.uuid("recipient_id")
    private val text: Column<String> = MessageModel.text("text")
    private val createdAt: Column<LocalDate> = MessageModel.date("created_at")

    fun create(messageDTO: MessageDTO) {
        transaction {
            MessageModel.insert {
                it[id] = messageDTO.id
                it[chatID] = messageDTO.chatID
                it[senderID] = messageDTO.senderID
                it[recipientID] = messageDTO.recipientID
                it[text] = messageDTO.text
                it[createdAt] = messageDTO.createdAt
            }
        }
    }

    fun fetchMessage(chatID: UUID): List<MessageDTO>? {
        return try {
            transaction {
                MessageModel.select { MessageModel.chatID.eq(chatID) }.map {MessageDTO(
                    id = it[MessageModel.id],
                    chatID = it[MessageModel.chatID],
                    senderID = it[MessageModel.senderID],
                    recipientID = it[MessageModel.recipientID],
                    text = it[MessageModel.text],
                    createdAt = it[MessageModel.createdAt]
                )}
            }
        } catch (e: Exception) {
            null
        }
    }


}





















