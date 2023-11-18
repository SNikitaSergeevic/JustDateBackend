package com.example.plugins.news

import com.example.feauteres.controllers.*
import com.example.feauteres.controllers.news.*
import com.example.feauteres.model.news.NewTagsSetReceiveRemote
import com.example.feauteres.model.news.NewTagsDTO
import com.example.feauteres.model.news.NewTagsCreateReceiveRemote
import com.example.plugins.Endpoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


fun Application.tagConfigure() {
    routing {
        authenticate("auth-jwt") {
            get(Endpoint.FetchCardsTags.str) {
                val cardID = call.parameters["cardID"]!!
                val respondTags = TagController().fetchCardTags(UUID.fromString(cardID))
                if (respondTags == null) {
                    call.respond(HttpStatusCode.Conflict, "responndTags == null")
                } else {
                    call.respond(HttpStatusCode.Accepted, respondTags)
                }
            }

            post(Endpoint.SetNewCardTags.str) {
                val newTags = call.receive<List<NewTagsCreateReceiveRemote>>()
                val createdTags = TagController().createTagsForCard(newTags)
                call.respond(HttpStatusCode.Created, createdTags)
            }

            post(Endpoint.SetExistCardTags.str) {
                val tags = call.receive<List<NewTagsSetReceiveRemote>>()
                TagController().setTagsForCard(tags.map {
                    NewTagsDTO(
                        UUID.randomUUID(),
                        UUID.fromString(it.cardID),
                        UUID.fromString(it.tagID)
                    )
                })
                call.respond(HttpStatusCode.Created, "Operation complete")
            }

        }
    }
}