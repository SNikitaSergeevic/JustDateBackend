package com.example.plugins.news

import com.example.feauteres.controllers.CardController
import com.example.plugins.Endpoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.cardConfigure() {
    routing {
        authenticate("auth-jwt") {
            post(Endpoint.FetchCardsOnSex.str) {
                try {
                    val cardController = CardController()
                    val cardResponse = cardController.fetchCardSex(call)
                    if (cardResponse != null) {
                        call.respond(cardResponse)
                    } else {
                        call.respond(HttpStatusCode.Conflict)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }

}