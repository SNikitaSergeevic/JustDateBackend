package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.model.OwnerModel
import com.example.feauteres.token.TokenController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class LoginReceiveRemote(val email: String, val password: String)

class LoginController(private val call: ApplicationCall) {
    suspend fun loginOwner(): Pair<UUID?, String> {
        val loginReceiveRemote = call.receive<LoginReceiveRemote>()
        val ownerModel = OwnerModel.fetchOwner(email = loginReceiveRemote.email)
        return if (ownerModel != null && ownerModel.password == loginReceiveRemote.password) {
            val tokenController = TokenController(loginReceiveRemote.email, ownerModel.id)
            tokenController.deleteRefreshToken()
            val refreshToken = tokenController.createRefreshToken()
             Pair(ownerModel.id, refreshToken)
        } else {
            call.respond(HttpStatusCode.Conflict, "Incorrect login or password")
            Pair(null, "")
        }
    }
}

fun Application.configureLogin() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    routing {
        post("/login") {
            val loginController = LoginController(call)
            val (ownerid, refreshToken) = loginController.loginOwner()

            if (ownerid != null) {
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("oid", ownerid.toString())
                    .withClaim("rt", refreshToken)
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC256(secret))
                val hash = hashMapOf("token" to token)
                call.respond(hash)
            }
        }
    }
}