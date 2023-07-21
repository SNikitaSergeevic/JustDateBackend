package com.example.feauteres.login

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginReceiveRemote(var email: String, val password: String) {

}

@Serializable
data class LoginResponceRemote(val token: String) {

}

fun Application.configureLoginRouting() {
    routing {
        post("/login") {
            val receive = call.receive<LoginReceiveRemote>()
            return@post call.respond(receive)
        }
    }
}