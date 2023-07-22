package com.example.plugins


import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureRouting() {
    configureRegistration()
    configureLogin()
    configureAuthorisation()
    routing {
        get("/check") {
            call.respondText("This Worked")
        }
        authenticate("auth-jwt") {
            get("/hello") {
                val principal = call.principal<JWTPrincipal>()
                val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())
                val id = principal?.get("oid")
                call.respondText("Token for $id expires at $expiresAt.")
            }
        }
    }
}
