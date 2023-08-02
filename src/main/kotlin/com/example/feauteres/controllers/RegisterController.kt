package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RegisterReceiveRemote(val email: String,
                                 val password: String,
                                 val name: String,
                                 val location: String,
                                 val description: String,
                                 val age: Int,
                                 val sex: String)

@Serializable
data class RegisterResponseRemote(val ownerid: String, val userpublicid: String, var refreshToken: String)

class RegisterController(private val call: ApplicationCall) {

    suspend fun registerOwner(): RegisterResponseRemote? {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()
        val ownerModel = OwnerModel.fetch(email = registerReceiveRemote.email)

        if (ownerModel != null) {
            call.respond(HttpStatusCode.Conflict, "User already exist")
            return null
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

            return RegisterResponseRemote(uuidForOwner.toString(), uuidForUserspublic.toString(), refreshToken)
        }
    }
}


