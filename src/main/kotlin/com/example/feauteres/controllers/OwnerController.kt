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

@Serializable
data class FetchOwnerRemote(val id: String)

@Serializable
data class FetchOwnerRespond(val id: String, val email: String, val password: String, val location: String, val userpublicid: String, val rToken: String)

class OwnerRemoteController(private val call: ApplicationCall) {

    suspend fun updateOwner() {
        println("updateOwner() START\n")
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
        call.respond(HttpStatusCode.OK, "Owner updated")
        print("Owner remote controller updateOwner() finish\n")
    }

    suspend fun fetchOwner(): FetchOwnerRespond? {
        println("fetchOwner() START")
        val id = call.receive<FetchOwnerRemote>().id
        val owner = OwnerModel.fetch(UUID.fromString(id))
        return if (owner != null) {
            val token = TokenModel.fetchToken(owner.id)
            return if (token != null) {
                 FetchOwnerRespond(id = owner.id.toString(), email = owner.email, location = owner.location, password = owner.password, userpublicid = owner.userpublicid.toString(), rToken = token.token.toString() )
            } else {
                null
            }
        } else {
            null
        }
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