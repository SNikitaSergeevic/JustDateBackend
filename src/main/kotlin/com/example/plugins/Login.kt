package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.controllers.LoginController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*



fun Application.configureLogin() {

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    routing {
        post(Endpoint.Login.str) {
            val loginController = LoginController(call)
            val ownerResponse = loginController.loginOwner()

            if (ownerResponse != null) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("oid", ownerResponse.ownerid)
                    .withClaim("upid", ownerResponse.userpulicid)
                    .withClaim("rt", ownerResponse.refreshToken)
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC256(secret))
//                val hash = hashMapOf("token" to token)
                ownerResponse.accessToken = token
                call.respond(ownerResponse)
            }
        }
    }
}