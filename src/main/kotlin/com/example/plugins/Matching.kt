package com.example.plugins

import com.example.feauteres.controllers.UserpublicController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureMatching() {
    routing {
        authenticate("auth-jwt") {
            post(Endpoint.FetchCardsOnSex.str) {
                try {
                    val userpublicController = UserpublicController(call)
                    val userpublicResponse = userpublicController.fetchUserpublicSex()
                    if (userpublicResponse != null) {
                        call.respond(userpublicResponse)
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