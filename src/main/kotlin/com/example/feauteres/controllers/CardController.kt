package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import java.time.LocalDate
import java.util.*


class CardController() {
    suspend fun fetchCardSex(localCall: ApplicationCall): CardResponse? {
        print("CardController fetchCardSex() START")

        val receive = localCall.receive<FetchCardSexReceiveRemote>()
        val cards = CardModel.fetchOnSex(receive.sex)

        if (cards != null) {
            var cardRemote: CardResponse = CardResponse(cards = emptyList())
            cardRemote.cards = cards.map {
                val imageResponseList = ImagesController().getAllIdImagesForCard(it.id.toString())?.let {it as? List<ImageResponse>} ?: emptyList()
                CardRemote(
                    id = it.id.toString(),
                    name = it.name,
                    description = it.description,
                    location = it.location,
                    age = it.age,
                    sex = it.sex,
                    imageIDs = imageResponseList,
                    createdAt = it.createdAt.toString(),
                    lastAuth = it.lastAuth.toString()
                )
            }
            return cardRemote
        } else {return null}

    }

//    suspend fun cardMatchCreate(localCall: ApplicationCall): MatchDTO? {
//        print("CardController cardMatchCreate() START")
//        val senderOwner = localCall.receive<MatchCreateReceiveRemote>()
//        val existingMatch = MatchModel.fetchRecipiend(UUID.fromString(senderOwner.senderID))
//
//        if (existingMatch != null) {
//            return existingMatch
//        } else {
//
//        }
//
//    }


}


class MatchController() {

    suspend fun getExistMatchSenderIDCheck(id: UUID): List<MatchDTO>? { // where sender is recipient
        return MatchModel.fetchSender(id)
    }

    suspend fun getExistMatchRecipientIDCheck(id: UUID): List<MatchDTO>? {
        return MatchModel.fetchRecipiend(id)
    }

    suspend fun getExistMatchSenRecIDChek(senID: UUID, recID: UUID): MatchDTO? {
        return MatchModel.fetchSenderRecipient(senID, recID)
    }

    suspend fun updateMatch(senCardID: UUID, recCardID: UUID): MatchDTO? {
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
                match = existMatchSenderIsSender.match.toString(),
                idSender = existMatchSenderIsSender.idSender.toString(),
                idRecipient = existMatchSenderIsSender.idRecipient.toString()
            )

        } else if (existMatchSenderIsRecipient != null) {
            existMatchSenderIsRecipient.recipientShow += 1
            existMatchSenderIsRecipient.match = true
            MatchModel.updateFromRecipient(existMatchSenderIsRecipient)

            // Chat create
            // notification for sender
            MatchResponse(
                id = existMatchSenderIsRecipient.id.toString(),
                cardIdSender = existMatchSenderIsRecipient.cardIdSender.toString(),
                cardIdRecipient = existMatchSenderIsRecipient.cardIdRecipient.toString(),
                recipientShow = existMatchSenderIsRecipient.recipientShow,
                senderShow = existMatchSenderIsRecipient.senderShow,
                match = existMatchSenderIsRecipient.match.toString(),
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
                    createdAt = LocalDate.now(),
                    idSender = UUID.fromString(matchCreate.idSender),
                    idRecipient = UUID.fromString(matchCreate.cardIdRecipient)
                )
                MatchModel.create(newMatch)
                MatchResponse(
                    id = newMatch.id.toString(),
                    cardIdSender = newMatch.cardIdSender.toString(),
                    cardIdRecipient = newMatch.cardIdRecipient.toString(),
                    recipientShow = newMatch.recipientShow,
                    senderShow = newMatch.senderShow,
                    match = newMatch.match.toString(),
                    idSender = newMatch.idSender.toString(),
                    idRecipient = newMatch.idRecipient.toString()
                )
            } else {
                null
            }

        }
    }

}







































