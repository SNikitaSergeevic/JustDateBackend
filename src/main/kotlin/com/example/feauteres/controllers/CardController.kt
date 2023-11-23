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
}







































