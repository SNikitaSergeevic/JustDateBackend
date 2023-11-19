package com.example.plugins.news

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.controllers.*
import com.example.plugins.Endpoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.ownerConfigure() {

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build())
            validate { credential ->
                if (credential.payload.getClaim("id").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultSchema, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    routing {
        post(Endpoint.Registration.str) {
            val ownerController = OwnerController()
            val regResponse = ownerController.registerOwner(call)
            if (regResponse != null) {
                call.respond(HttpStatusCode.OK, "user success")
            }
        }

        post(Endpoint.Authorisation.str) {
            val ownerController = OwnerController()
            val authResponse = ownerController.authorisationOwnerWithRT(call)

            if (authResponse != null) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("oid", authResponse.id)
                    .withClaim("cid", authResponse.cardID)
                    .withClaim("rt", authResponse.refreshToken)
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC256(secret))
                authResponse.accessToken = token
                call.respond(authResponse)
            }

        }

        post(Endpoint.Login.str) {
            val ownerController = OwnerController()
            val ownerResponse = ownerController.loginOwner(call)

            if (ownerResponse != null) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("oid", ownerResponse.id)
                    .withClaim("cid", ownerResponse.cardID)
                    .withClaim("rt", ownerResponse.refreshToken)
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC256(secret))

                ownerResponse.accessToken = token
                call.respond(ownerResponse)
            }

        }

        authenticate("auth-jwt") {
            get(Endpoint.FetchPublicOwner.str) {
                val ownerController = OwnerController()
                val owner = ownerController.fetchPublicOwner(call)

                if (owner != null) {
                    call.respond(owner)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }

            }

            post(Endpoint.FetchPrivateOwner.str) {

            }

            post(Endpoint.UpdateOwner.str) {
                try {
                    val ownerController = OwnerController()
                    ownerController.updateOwner(call)
                    call.respond(HttpStatusCode.OK, "Owner updated")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict)
                }
            }

            post(Endpoint.DeleteOwner.str) {
                val ownerController = OwnerController()
                ownerController.deleteOwner(call)
            }

        }

    }


}




























