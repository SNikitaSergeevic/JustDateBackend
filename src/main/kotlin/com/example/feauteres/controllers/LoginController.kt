package com.example.feauteres.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import com.example.feauteres.model.*

@Serializable
data class LoginReceiveRemote(val email: String, val password: String)

class LoginController(private val call: ApplicationCall) {
    suspend fun loginOwner(): OwnerAuthResponse? {
        val loginReceiveRemote = call.receive<LoginReceiveRemote>()
        val ownerDTO = OwnerModel.fetch(email = loginReceiveRemote.email)
        return if (ownerDTO != null && ownerDTO.password == loginReceiveRemote.password) {
            val userpublic = UserspublicModel.fetch(ownerDTO.userpublicid.toString())
            if (userpublic != null) {
                val tokenController = TokenController(loginReceiveRemote.email, ownerDTO.id)
                tokenController.deleteRefreshToken()
                val refreshToken = tokenController.createRefreshToken()
                OwnerAuthResponse(ownerDTO.id.toString(), ownerDTO.userpublicid.toString(), refreshToken, userpublic.name, userpublic.description, userpublic.location, userpublic.age, userpublic.sex, "")
            } else {
                null
            }
        } else {
            call.respond(HttpStatusCode.Conflict, "Incorrect login or password")
             null
        }
    }
}

/*

data class OwnerAuthResponse (val ownerid: String, val userpulicid: String, var refreshToken: String, val name: String, val description: String, val location: String, val age: Int, val sex: String, var accessToken: String) 


 */