package com.example.feauteres.controllers.news

import com.example.feauteres.model.*
import com.example.feauteres.controllers.ImagesController
import com.example.feauteres.model.news.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import java.util.*




class CardController(private val call: ApplicationCall) {
    suspend fun fetchCardSex(): CardResponse? {
        print("CardController fetchCardSex() START")

        val receive = call.receive<FetchCardSexReceiveRemote>()
        val cards = CardModel.fetchOnSex(receive.sex)

        if (cards != null) {
            var cardRemote: CardResponse = CardResponse(cards = emptyList())
            cardRemote.cards = cards.map {
                val imageResponseList = NewImagesController().getAllIdImagesForCard(it.id.toString())?.let {it as? List<ImageResponse>} ?: emptyList()
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