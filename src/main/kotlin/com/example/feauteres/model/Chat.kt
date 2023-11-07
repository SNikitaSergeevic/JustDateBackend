package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Date
import java.time.DateTimeException
import java.util.UUID

class ChatDTO (val id: UUID, val ownerID: UUID, val companionID: UUID, val createdAt: Date)

object ChatModel: Table("justdate_schema.chat")  { // chat not exist in DB
    private val id = ChatModel.uuid("id")
    private val ownerID = ChatModel.uuid("ownerID")
    private val companionID = ChatModel.uuid("companionID")
//    private val createdAt = timestamp("created_at")
    private val createdAt = ChatModel.timestamp("created_at")
}