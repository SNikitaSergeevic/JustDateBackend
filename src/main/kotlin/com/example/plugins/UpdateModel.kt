package com.example.plugins

import com.example.feauteres.controllers.OwnerRemoteController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureUpdateModel() {

    routing {
        authenticate("auth-jwt") {
            post(Endpoint.UpdateOwner.str) {
                try {
                    val updateController = OwnerRemoteController(call)
                    updateController.updateOwner()
                    call.respond(HttpStatusCode.OK, "Owner updated")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict)
                }

            }

            post(Endpoint.DeleteOwner.str) {
                val ownerController = OwnerRemoteController(call)
                ownerController.deleteOwner()
            }
        }
    }

}