package com.example.feauteres.controllers


import com.example.feauteres.model.OwnerAuthResponse
import com.example.feauteres.model.OwnerModel
import com.example.feauteres.model.UserspublicModel
import com.example.feauteres.model.TokenModel
import kotlinx.serialization.Serializable
import io.ktor.server.application.*
import io.ktor.server.request.*
import java.util.*

@Serializable
data class AuthorisationReceiveRemote(val ownerid: String, val refreshToken: String)


class AuthorisationController(private val call: ApplicationCall) {
    suspend fun authorisationOwnerWithRT(): OwnerAuthResponse? {
        val authorisationReceiveRemote = call.receive<AuthorisationReceiveRemote>()
        val tokenDTO = TokenModel.fetchToken(UUID.fromString(authorisationReceiveRemote.ownerid))

        if (tokenDTO != null) {
            if (authorisationReceiveRemote.refreshToken.toInt() == tokenDTO.token.toInt()) { // FIXME check toInt Result
                val ownerDTO = OwnerModel.fetch(UUID.fromString(authorisationReceiveRemote.ownerid))
                if (ownerDTO != null) {
                    val userpublic = UserspublicModel.fetch(ownerDTO.userpublicid.toString())
                    if (userpublic != null) {
                        val tokenController = TokenController(ownerDTO!!.email, ownerDTO!!.id)
                        if (tokenController.checkRefreshToken()) tokenController.deleteRefreshToken() else return null

                        val refreshToken = tokenController.createRefreshToken()

                        return OwnerAuthResponse(ownerDTO.id.toString(), userpublic.id.toString(), refreshToken, userpublic.name, userpublic.description, userpublic.location, userpublic.age, userpublic.sex, "")
                    } else {
                        return null
                    }
                }
            }
            return null
        } else {
            return null
        }
    }
}