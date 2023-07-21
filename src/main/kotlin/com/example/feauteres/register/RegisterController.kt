package com.example.feauteres.register

import com.example.feauteres.model.*
import com.example.feauteres.token.TokenController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.h2.engine.User
import java.util.*

@Serializable
data class RegisterReceiveRemote(val email: String,
                                 val password: String,
                                 val name: String,
                                 val location: String,
                                 val description: String,
                                 val age: Int,
                                 val sex: String)

data class RegisterResponseRemote(val token: String)

class RegisterController(private val call: ApplicationCall) {

    suspend fun registerOwner(): Pair<UUID?, String> {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()
        val ownerModel = OwnerModel.fetchOwner(email = registerReceiveRemote.email)

        if (ownerModel != null) {
            call.respond(HttpStatusCode.Conflict, "User already exist")
            return Pair(null, "")
        } else {
            val uuidForOwner = UUID.randomUUID()
            val uuidForUserspublic = UUID.randomUUID()
            val tokenController = TokenController(registerReceiveRemote.email, uuidForOwner)

            val userpublicDTO = UserspublicDTO(
                id = uuidForUserspublic,
                name = registerReceiveRemote.name,
                description = registerReceiveRemote.description,
                location = registerReceiveRemote.location,
                age = registerReceiveRemote.age,
                sex = registerReceiveRemote.sex
            )
            val ownerDTO = OwnerDTO(
                id = uuidForOwner,
                email = registerReceiveRemote.email,
                password = registerReceiveRemote.password,
                location = registerReceiveRemote.location,
                userpublicid = uuidForUserspublic
            )

            UserspublicModel.insert(userpublicDTO)
            OwnerModel.insert(ownerDTO)
            val refreshToken = tokenController.createRefreshToken()

            return Pair(uuidForOwner, refreshToken)
        }
    }
}


