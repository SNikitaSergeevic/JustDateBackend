package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.model.OwnerModel
import com.example.feauteres.model.TokenModel
import com.example.feauteres.token.TokenController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AuthorisationReceiveRemote(val ownerid: String, val rtoken: String)

class AuthorisationController(private val call: ApplicationCall) {
    suspend fun authorisationOwnerWithRT(): Pair<UUID?, String> {
        val authorisationReceiveRemote = call.receive<AuthorisationReceiveRemote>()
        val tokenDTO = TokenModel.fetchToken(UUID.fromString(authorisationReceiveRemote.ownerid))

        if (tokenDTO != null) {
            if (authorisationReceiveRemote.rtoken.toInt() == tokenDTO.token.toInt()) { // FIXME check toInt Result
                val ownerDTO = OwnerModel.fetchOwner(UUID.fromString(authorisationReceiveRemote.ownerid))
                if (ownerDTO != null) {
                    val tokenController = TokenController(ownerDTO!!.email, ownerDTO!!.id)
                    tokenController.deleteRefreshToken()
                    val refreshToken = tokenController.createRefreshToken()
                    return Pair(ownerDTO.id, refreshToken)
                }
            }
           return Pair(null, "")
        } else {
            return Pair(null, "")
        }
    }

}

fun Application.configureAuthorisation() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    routing {
        post("/auth") {
            val authController = AuthorisationController(call)
            val (ownerid, refreshToken) = authController.authorisationOwnerWithRT()

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