package com.example.plugins.news

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.controllers.*
import com.example.feauteres.controllers.news.*
import com.example.feauteres.model.news.NewTags
import com.example.feauteres.model.news.NewTagsDTO
import com.example.feauteres.model.news.NewTagsReceiveRemote
import com.example.feauteres.model.news.TagRemoteResponse
import com.example.plugins.Endpoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


fun Application.tagConfiguration() {
    routing {
        authenticate {
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
                val newTags = call.receive<List<NewTagsReceiveRemote>>()
                val createdTags = TagController().createTagsForCard(newTags)
                call.respond(HttpStatusCode.Created, createdTags)
            }

            post(Endpoint.SetExistCardTags.str) {
                val tags = call.receive<List<NewTags>>()
                TagController().setTagsForCard(tags.map {
                    NewTagsDTO(
                        UUID.fromString(it.id),
                        UUID.fromString(it.cardID),
                        UUID.fromString(it.tagID)
                    )
                })
                call.respond(HttpStatusCode.Created, "Operation complete")
            }

        }
    }
}