package com.example.plugins.news

import com.example.feauteres.controllers.CardController
import com.example.feauteres.controllers.MatchController
import com.example.feauteres.model.MatchCreateReceiveRemote
import com.example.feauteres.model.MatchResponse
import com.example.plugins.Endpoint
import kotlinx.serialization.Serializable
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


fun Application.matchConfigure() {
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

            get(Endpoint.FetchExistMatchSenderIDCheck.str) {
                try {
                    val matchController = MatchController()
                    val id = call.parameters["id"]
                    val matches = matchController.getExistMatchSenderIDCheck(UUID.fromString(id))

                    if (matches != null) {
                        call.respond(HttpStatusCode.Accepted, matches.map {
                            MatchResponse(
                                id = it.id.toString(),
                                cardIdSender = it.cardIdSender.toString(),
                                cardIdRecipient = it.cardIdRecipient.toString(),
                                recipientShow = it.recipientShow,
                                senderShow = it.senderShow,
                                match = it.match,
                                idSender = it.idSender.toString(),
                                idRecipient = it.idRecipient.toString()
                            )
                        })
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, e)
                }
            }

            get(Endpoint.FetchExistMatchRecipientIDCheck.str) {
                try {
                    val matchController = MatchController()
                    val id = call.parameters["id"]
                    val matches = matchController.getExistMatchRecipientIDCheck(UUID.fromString(id))

                    if (matches != null) {
                        call.respond(HttpStatusCode.Accepted, matches.map {
                            MatchResponse(
                                id = it.id.toString(),
                                cardIdSender = it.cardIdSender.toString(),
                                cardIdRecipient = it.cardIdRecipient.toString(),
                                recipientShow = it.recipientShow,
                                senderShow = it.senderShow,
                                match = it.match,
                                idSender = it.idSender.toString(),
                                idRecipient = it.idRecipient.toString()
                            )
                        })
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, e)
                }
            }

            put(Endpoint.UpdateMatch.str) {
                try {
                    val matchController = MatchController()
                    val senCardID = call.parameters["senCardID"]
                    val recCardID = call.parameters["recCardID"]
                    val match = matchController.updateMatch(UUID.fromString(senCardID), UUID.fromString(recCardID))


                    if (match != null) {
                        call.respond(HttpStatusCode.Accepted, MatchResponse(
                            id = match.id.toString(),
                            cardIdSender = match.cardIdSender.toString(),
                            cardIdRecipient = match.cardIdRecipient.toString(),
                            recipientShow = match.recipientShow,
                            senderShow = match.senderShow,
                            match = match.match,
                            idSender = match.idSender.toString(),
                            idRecipient = match.idRecipient.toString()
                        ))
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict)
                }
            }

            post(Endpoint.CreateMatch.str) {
                try {
                    val newMatchReceiveRemote = call.receive<MatchCreateReceiveRemote>()
                    val matchController = MatchController()
                    val match = matchController.createMatch(newMatchReceiveRemote)

                    if (match != null) {
                        println("CreateMatch ${match.idSender}")
                        call.respond(match)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }


                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, e)
                }
            }

        }
    }
}



















