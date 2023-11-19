package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.UUID

@Serializable
data class MatchCreateReceiveRemote(
    val senderID: String,
    val recipientID: String
)

class MatchDTO(
    val id: UUID,
    val cardIdSender: UUID, // who liken first
    val cardIdRecipient: UUID,
    val recipientShow: Int,
    val senderShow: Int,
    val match: Boolean,
    val createdAt: LocalDate,
    val idSender: UUID,
    val idRecipient: UUID
)

object MatchModel: Table("match") {
    private val id: Column<UUID> = MatchModel.uuid("id")
    private val cardIdSender: Column<UUID> = MatchModel.uuid("card_id_sen")
    private val cardIdRecipient: Column<UUID> = MatchModel.uuid("card_id_rec")
    private val recipientShow: Column<Int> = MatchModel.integer("rec_show")
    private val senderShow: Column<Int> = MatchModel.integer("sen_show")
    private val match: Column<Boolean> = MatchModel.bool("match")
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
                MatchModel.select {MatchModel.idSender.eq(recipientID)}.map {
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

    fun fetchSenderRecipient(senID: UUID, recID: UUID): MatchDTO? {
        return try {
            transaction {
                val executeMatch =
                    MatchModel.select { (MatchModel.idSender eq senID) and (MatchModel.idRecipient eq recID) }.single()

                MatchDTO(
                    id = executeMatch[MatchModel.id],
                    cardIdSender = executeMatch[MatchModel.cardIdSender],
                    cardIdRecipient = executeMatch[MatchModel.cardIdRecipient],
                    recipientShow = executeMatch[MatchModel.recipientShow],
                    senderShow = executeMatch[MatchModel.senderShow],
                    match = executeMatch[MatchModel.match],
                    createdAt = executeMatch[MatchModel.createdAt],
                    idSender = executeMatch[MatchModel.idSender],
                    idRecipient = executeMatch[MatchModel.idRecipient]
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
                    it[recipientShow] = matchDTO.recipientShow
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





























