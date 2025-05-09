package com.example.feauteres.controllers

import com.example.feauteres.model.*
import java.time.LocalDate
import java.util.*
import io.ktor.server.application.*
import io.ktor.server.request.*

class MatchController() {

    suspend fun getExistMatchSenderIDCheck(id: UUID): List<MatchDTO>? { // where sender is recipient
        println("\n MatchController getExistMatchSenderIDCheck(id: UUID) START")
        return MatchModel.fetchSender(id)
    }

    suspend fun getExistMatchRecipientIDCheck(id: UUID): List<MatchDTO>? {
        println("\n MatchController getExistMatchRecipientIDCheck(id: UUID) START")
        return MatchModel.fetchRecipiend(id)
    }

    suspend fun getExistMatchSenRecIDChek(senID: UUID, recID: UUID): MatchDTO? {
        println("\n MatchController getExistMatchSenRecIDChek(senID: UUID, recID: UUID) START")

        return MatchModel.fetchSenderRecipient(senID, recID)
    }

    suspend fun updateMatch(senCardID: UUID, recCardID: UUID): MatchDTO? {
        println("\n MatchController updateMatch(senCardID: UUID, recCardID: UUID) START")

        val matchSenderIsSender = getExistMatchSenRecIDChek(senCardID, recCardID)
        val matchSenderIsRecipient = getExistMatchSenRecIDChek(recCardID, senCardID)
        return if (matchSenderIsSender != null) {
            matchSenderIsSender.senderShow += 1
            MatchModel.updateFromSender(matchSenderIsSender)
            matchSenderIsSender
        } else if (matchSenderIsRecipient != null) {
            matchSenderIsRecipient.recipientShow += 1
            matchSenderIsRecipient.match = true
            MatchModel.updateFromRecipient(matchSenderIsRecipient)
            matchSenderIsRecipient
        } else {
            println("NOT Match UPDATE BECAUSE NOT EXIST")
            null
        }
    }

    suspend fun createMatch(matchCreate: MatchCreateReceiveRemote): MatchResponse? {
        println("\n MatchController createMatch(matchCreate: MatchCreateReceiveRemote) START")
        //fixme: create simple and correct chat create


        val existMatchSenderIsSender =
            MatchModel.fetchSenderRecipient(
                UUID.fromString(matchCreate.cardIdSender),
                UUID.fromString(matchCreate.cardIdRecipient)
            )

        val existMatchSenderIsRecipient =
            MatchModel.fetchSenderRecipient(
                UUID.fromString(matchCreate.cardIdRecipient),
                UUID.fromString(matchCreate.cardIdSender)
            )

        return if (existMatchSenderIsSender != null) {
            existMatchSenderIsSender.senderShow += 1
            MatchModel.updateFromSender(existMatchSenderIsSender)
            MatchResponse(
                id = existMatchSenderIsSender.id.toString(),
                cardIdSender = existMatchSenderIsSender.cardIdSender.toString(),
                cardIdRecipient = existMatchSenderIsSender.cardIdRecipient.toString(),
                recipientShow = existMatchSenderIsSender.recipientShow,
                senderShow = existMatchSenderIsSender.senderShow,
                match = existMatchSenderIsSender.match,
                idSender = existMatchSenderIsSender.idSender.toString(),
                idRecipient = existMatchSenderIsSender.idRecipient.toString()
            )

        } else if (existMatchSenderIsRecipient != null) {
            println("\n existMatchSenderIsRecipient \n")

            existMatchSenderIsRecipient.recipientShow += 1
            existMatchSenderIsRecipient.match = true
            MatchModel.updateFromRecipient(existMatchSenderIsRecipient)

            val currentDate = java.util.Date().time
            val chatOwnerIsSender = ChatDTO(
                id = UUID.randomUUID(),
                ownerID = existMatchSenderIsRecipient.idSender,
                companionID = existMatchSenderIsRecipient.idRecipient,
                createdAt = currentDate
            )

            val chatOwnerIsRecipient = ChatDTO(
                id = UUID.randomUUID(),
                ownerID = existMatchSenderIsRecipient.idRecipient,
                companionID = existMatchSenderIsRecipient.idSender,
                createdAt = currentDate
            )
            ChatModel.create(chatOwnerIsRecipient)
            ChatModel.create(chatOwnerIsSender)

            MatchResponse(
                id = existMatchSenderIsRecipient.id.toString(),
                cardIdSender = existMatchSenderIsRecipient.cardIdSender.toString(),
                cardIdRecipient = existMatchSenderIsRecipient.cardIdRecipient.toString(),
                recipientShow = existMatchSenderIsRecipient.recipientShow,
                senderShow = existMatchSenderIsRecipient.senderShow,
                match = existMatchSenderIsRecipient.match,
                idSender = existMatchSenderIsRecipient.idSender.toString(),
                idRecipient = existMatchSenderIsRecipient.idRecipient.toString()
            )
        } else {
            val recipient = OwnerModel.fetchOnCradid(UUID.fromString(matchCreate.cardIdRecipient))

            if (recipient != null) {
                val newMatch = MatchDTO(
                    id = UUID.randomUUID(),
                    cardIdSender = UUID.fromString(matchCreate.cardIdSender),
                    cardIdRecipient = UUID.fromString(matchCreate.cardIdRecipient),
                    recipientShow = 0,
                    senderShow = 1,
                    match = false,
                    createdAt = java.util.Date().time,
                    idSender = UUID.fromString(matchCreate.idSender),
                    idRecipient = recipient.id
                )
                MatchModel.create(newMatch)

                MatchResponse(
                    id = newMatch.id.toString(),
                    cardIdSender = newMatch.cardIdSender.toString(),
                    cardIdRecipient = newMatch.cardIdRecipient.toString(),
                    recipientShow = newMatch.recipientShow,
                    senderShow = newMatch.senderShow,
                    match = newMatch.match,
                    idSender = newMatch.idSender.toString(),
                    idRecipient = newMatch.idRecipient.toString()
                )
            } else {
                null
            }

        }
    }

}