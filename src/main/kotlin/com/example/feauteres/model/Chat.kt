package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Date
import java.time.DateTimeException
import java.time.LocalDate
import java.util.UUID


//TODO: ChatModel
class ChatDTO (val id: UUID,
               val ownerID: UUID,
               val companionID: UUID,
               val createdAt: Date)

object ChatModel: Table("chat")  { // chat not exist in DB
    private val id: Column<UUID> = ChatModel.uuid("id")
    private val ownerID: Column<UUID> = ChatModel.uuid("owner_id")
    private val companionID: Column<UUID> = ChatModel.uuid("companion_id")
    private val createdAt: Column<LocalDate> = ChatModel.date("created_at")
}



//TODO: MessageModel

class MessageDTO (val id: UUID,
                  val chatID: UUID,
                  val senderID: UUID,
                  val receiverID: UUID,
                  val text: String,
                  val createdAt: Date)

object MessageModel: Table("message") {
    private val id: Column<UUID> = MessageModel.uuid("id")
    private val chatID: Column<UUID> = MessageModel.uuid("chat_id")
    private val senderID: Column<UUID> = MessageModel.uuid("sender_id")
    private val recipientID: Column<UUID> = MessageModel.uuid("recipient_id")
    private val text: Column<String> = MessageModel.text("text")
    private val createdAt: Column<LocalDate> = MessageModel.date("created_at")
}






