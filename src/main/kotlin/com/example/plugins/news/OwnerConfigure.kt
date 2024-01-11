package com.example.plugins.news

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.controllers.*
import com.example.plugins.Endpoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import java.util.*


fun Application.ownerConfigure(
    secret: String,
    issuer: String,
    audience: String,
    myRealm: String
) {

    routing {
        post(Endpoint.Registration.str) {
            val ownerController = OwnerController()
            val regResponse = ownerController.registerOwner(call)
            if (regResponse != null) {
                call.respond(HttpStatusCode.OK, regResponse)
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
        swaggerUI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }
    }

}




























