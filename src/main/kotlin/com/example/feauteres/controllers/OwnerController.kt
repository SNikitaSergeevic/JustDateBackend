package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UpdateOwnerRemote(val id: String,
                             val email: String,
                             val password: String,
                             val name: String,
                             val location: String,
                             val description: String,
                             val age: Int,
                             val sex: String,
                             val userspublicid: String)

@Serializable
data class DeleteOwnerRemote(val id: String, val upid: String)

class OwnerRemoteController(private val call: ApplicationCall) {

    suspend fun updateOwner() {
        print("Owner remote controller updateOwner() start\n")
        val updateRemote = call.receive<UpdateOwnerRemote>()
        OwnerModel.update(OwnerDTO(id = UUID.fromString(updateRemote.id),
                                        email = updateRemote.email,
                                        location = updateRemote.location,
                                        password = updateRemote.password,
                                        userpublicid = UUID.fromString(updateRemote.userspublicid)))
        UserspublicModel.update(UserspublicDTO(id = UUID.fromString(updateRemote.userspublicid),
                                                name = updateRemote.name,
                                                description = updateRemote.description,
                                                location = updateRemote.location,
                                                age = updateRemote.age,
                                                sex = updateRemote.sex))
        print("Owner remote controller updateOwner() finish\n")
    }

    suspend fun deleteOwner() {
        print("deleteOwner start \n")
        try {
            val deleteRemote = call.receive<DeleteOwnerRemote>()
            TokenModel.deleteToken(UUID.fromString(deleteRemote.id))
            OwnerModel.delete(UUID.fromString(deleteRemote.id))
            UserspublicModel.delete(UUID.fromString(deleteRemote.upid))
            call.respond(HttpStatusCode.OK, "Owner deleted")
        } catch(e: Exception) {

        }

    }

}