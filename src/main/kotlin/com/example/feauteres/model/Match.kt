package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.UUID

@Serializable
data class MatchCreateReceiveRemote(
    val cardIdSender: String,
    val cardIdRecipient: String,
    val idSender: String
)

@Serializable
data class MatchResponse(
    val id: String,
    val cardIdSender: String, // who liken first
    val cardIdRecipient: String,
    var recipientShow: Int,
    var senderShow: Int,
    var match: Boolean,
    val createdAt: String,
    val idSender: String,
    val idRecipient: String
)

class MatchDTO(
    val id: UUID,
    val cardIdSender: UUID, // who liken first
    val cardIdRecipient: UUID,
    var recipientShow: Int,
    var senderShow: Int,
    var match: Boolean,
    val createdAt: LocalDate,
    val idSender: UUID,
    val idRecipient: UUID
)

object MatchModel: Table("match") {
    private val id: Column<UUID> = MatchModel.uuid("id")
    private val cardIdSender: Column<UUID> = MatchModel.uuid("card_id_sen")
    private val cardIdRecipient: Column<UUID> = MatchModel.uuid("card_id_rec")
    private var recipientShow: Column<Int> = MatchModel.integer("rec_show")
    private var senderShow: Column<Int> = MatchModel.integer("sen_show")
    private var match: Column<Boolean> = MatchModel.bool("match")
    private val createdAt: Column<LocalDate> = MatchModel.date("created_at")
    private val idSender: Column<UUID> = MatchModel.uuid("id_sen")
    private val idRecipient: Column<UUID> = MatchModel.uuid("id_rec")

    fun create(matchDTO: MatchDTO) {
        MatchModel.insert {
            it[id] = matchDTO.id
            it[cardIdSender] = matchDTO.cardIdSender
            it[cardIdRecipient] = matchDTO.cardIdRecipient
            it[recipientShow] = matchDTO.recipientShow
            it[senderShow] = matchDTO.senderShow
            it[match] = matchDTO.match
            it[createdAt] = matchDTO.createdAt
            it[idSender] = matchDTO.idSender
            it[idRecipient] = matchDTO.idRecipient
        }
    }

    fun fetchSender(senderID: UUID): List<MatchDTO>? {
        return try {
            transaction {
                MatchModel.select { MatchModel.idSender.eq(senderID) }.map {
                    MatchDTO(
                        id = it[MatchModel.id],
                        cardIdSender = it[MatchModel.cardIdSender],
                        cardIdRecipient = it[MatchModel.cardIdRecipient],
                        recipientShow = it[MatchModel.recipientShow],
                        senderShow = it[MatchModel.senderShow],
                        match = it[MatchModel.match],
                        createdAt = it[MatchModel.createdAt],
                        idSender = it[MatchModel.idSender],
                        idRecipient = it[MatchModel.idRecipient]
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchRecipiend(recipientID: UUID): List<MatchDTO>? {
        return try {
            transaction {
                MatchModel.select {MatchModel.idRecipient.eq(recipientID)}.map {
                    MatchDTO(
                        id = it[MatchModel.id],
                        cardIdSender = it[MatchModel.cardIdSender],
                        cardIdRecipient = it[MatchModel.cardIdRecipient],
                        recipientShow = it[MatchModel.recipientShow],
                        senderShow = it[MatchModel.senderShow],
                        match = it[MatchModel.match],
                        createdAt = it[MatchModel.createdAt],
                        idSender = it[MatchModel.idSender],
                        idRecipient = it[MatchModel.idRecipient]
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchSenderRecipient(senCardID: UUID, recCardID: UUID): MatchDTO? {
        return try {
            transaction {
                val existMatch =
                    MatchModel.select { (MatchModel.cardIdSender eq senCardID) and (MatchModel.cardIdRecipient eq recCardID) }.single()

                MatchDTO(
                    id = existMatch[MatchModel.id],
                    cardIdSender = existMatch[MatchModel.cardIdSender],
                    cardIdRecipient = existMatch[MatchModel.cardIdRecipient],
                    recipientShow = existMatch[MatchModel.recipientShow],
                    senderShow = existMatch[MatchModel.senderShow],
                    match = existMatch[MatchModel.match],
                    createdAt = existMatch[MatchModel.createdAt],
                    idSender = existMatch[MatchModel.idSender],
                    idRecipient = existMatch[MatchModel.idRecipient]
                )

            }
        } catch (e: Exception) {
            null
        }
    }

    fun updateFromSender(matchDTO: MatchDTO) {
        try {
            transaction {
                MatchModel.update ({ MatchModel.idSender eq matchDTO.idSender }) {
                    it[senderShow] = matchDTO.senderShow
                }
            }
        } catch (e: Exception) {

        }
    }

    fun updateFromRecipient(matchDTO: MatchDTO) {
        try {
            transaction {
                MatchModel.update ({ MatchModel.idRecipient eq matchDTO.idRecipient }) {
                    it[recipientShow] = matchDTO.recipientShow
                    it[match] = matchDTO.match
                }
            }
        } catch (e: Exception) {

        }
    }

}





























