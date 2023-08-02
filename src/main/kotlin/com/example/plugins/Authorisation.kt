package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.controllers.AuthorisationController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*



fun Application.configureAuthorisation() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    routing {
        post(Endpoint.Authorisation.str) {
            val authController = AuthorisationController(call)
            val authResponse = authController.authorisationOwnerWithRT()

            if (authResponse != null) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("oid", authResponse.ownerid)
                    .withClaim("upid", authResponse.userpulicid)
                    .withClaim("rt", authResponse.refreshToken)
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC256(secret))
//                val hash = hashMapOf("token" to token)
                authResponse.accessToken = token
                authResponse.refreshToken = "at"
                call.respond(authResponse)
            }
        }
    }
}