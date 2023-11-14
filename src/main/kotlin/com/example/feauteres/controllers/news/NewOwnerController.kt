package com.example.feauteres.controllers.news

import com.example.feauteres.controllers.LoginReceiveRemote
import com.example.feauteres.model.news.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.time.LocalDate
import java.util.*

class NewOwnerController(private val call: ApplicationCall) {

    suspend fun updateOwner() {
        println("NewOwnerController updateOwner() START")

        var updateRemote = call.receive<UpdateNewOwnerRemote>()

        NewOwnerModel.update(
            NewOwnerDTO(id = UUID.fromString(updateRemote.id, ),
            email = updateRemote.email,
            password = updateRemote.password,
            location = updateRemote.location,
            cardID = UUID.fromString(updateRemote.cardID),
            createdAt = LocalDate.now())
        )
        CardModel.update(
            CardDTO(id = UUID.fromString(updateRemote.cardID),
            name = updateRemote.name,
            description = updateRemote.description,
            location = updateRemote.location,
            age = updateRemote.age,
            sex = updateRemote.sex,
            createdAt = LocalDate.now(),
            lastAuth = LocalDate.now())
        )
        call.respond(HttpStatusCode.OK, "Owner updated")
    }

    suspend fun fetchOwner(): FetchNewOwnerRespond? {
        println("NewOwnerController fetchOwner() START")

        val id = call.receive<FetchNewOwnerRemote>().id
        val owner = NewOwnerModel.fetch(UUID.fromString(id))

        return if (owner != null) {
            val token = RefreshTokenModel.fetch(owner.id)
            return if (token != null) {
                FetchNewOwnerRespond(
                    id = owner.id.toString(),
                    email = owner.email,
                    location = owner.location,
                    password = owner.password,
                    cardID =  owner.cardID.toString(),
                    refreshToken = token.token
                )
            } else {
                null
            }
        } else {
            null
        }
    }

    suspend fun deleteOwner() {
        println("NewOwnerController deleteOwner() START")

        try {
            val deleteRemote = call.receive<DeleteNewOwnerRemote>()
            RefreshTokenModel.deleteToken(UUID.fromString(deleteRemote.id))
            NewOwnerModel.delete(UUID.fromString(deleteRemote.id))
            CardModel.delete(UUID.fromString(deleteRemote.cardID))
            call.respond(HttpStatusCode.OK, "Owner deleted")
        } catch (e: Exception) {

        }
    }

    suspend fun registerOwner(): NewOwnerRegisterResponseRemote? {
        var register = call.receive<NewOwnerRegisterReceiveRemote>()
        val ownerModel = NewOwnerModel.fetch(email = register.email)

        if (ownerModel != null) {
            call.respond(HttpStatusCode.Conflict, "User already exist")
            return null
        } else {
            val idForOwner = UUID.randomUUID()
            val idForCard = UUID.randomUUID()
            val tokenController = RefreshTokenController(register.email, idForOwner)

            val cardDTO = CardDTO(
                id = idForCard,
                name = register.name,
                description = register.description,
                location = register.location,
                age = register.age,
                sex = register.sex,
                createdAt = LocalDate.now(),
                lastAuth = LocalDate.now()
            )

            val newOwnerDTO = NewOwnerDTO(
                id = idForOwner,
                email = register.email,
                password = register.password,
                location = register.location,
                cardID = idForCard,
                createdAt = LocalDate.now()
            )

            CardModel.create(cardDTO)
            NewOwnerModel.create(newOwnerDTO)
            val refreshToken = tokenController.createRefreshToken()
            return NewOwnerRegisterResponseRemote(idForOwner.toString(), idForCard.toString(), refreshToken)
        }

    }


    suspend fun authorisationOwnerWithRT(): NewOwnerAuthResponse? {
        println("NewOwnerController authorisationOwnerWithRT() START")

        val authCall = call.receive<NewOwnerAuthReceiveRemote>()
        val refreshTokenDTO = RefreshTokenModel.fetch(UUID.fromString(authCall.ownerID))

        if (refreshTokenDTO != null) {
            if (authCall.refreshToken.toInt() == refreshTokenDTO.token.toInt()) {
                val ownerDTO = NewOwnerModel.fetch(UUID.fromString(authCall.ownerID))
                if (ownerDTO != null) {
                    val card = CardModel.fetch(ownerDTO.cardID.toString())
                    if (card != null) {
                        val refreshTokenController = RefreshTokenController(ownerDTO!!.email, ownerDTO!!.id)
                        if (refreshTokenController.checkRefreshToken()) refreshTokenController.deleteRefreshToken() else return null
                        val refreshToken = refreshTokenController.createRefreshToken()
                        return NewOwnerAuthResponse(ownerDTO.id.toString(),
                            card.id.toString(),
                            refreshToken,
                            "",
                            card.name,
                            card.description,
                            card.location,
                            card.age,
                            card.sex)
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

    suspend fun loginOwner(): NewOwnerAuthResponse? {
        println("NewOwnerController loginOwner() START")

        val loginReceiveRemote = call.receive<NewOwnerLoginReceiveRemote>()
        val ownerDTO = NewOwnerModel.fetch(email = loginReceiveRemote.email)

        return if (ownerDTO != null && ownerDTO.password == loginReceiveRemote.password) {
            val card = CardModel.fetch(ownerDTO.cardID.toString())
            if (card != null) {
                val tokenController = RefreshTokenController(loginReceiveRemote.email, ownerDTO.id)
                tokenController.deleteRefreshToken()
                val refreshToken = tokenController.createRefreshToken()
                NewOwnerAuthResponse(ownerDTO.id.toString(),
                    ownerDTO.cardID.toString(),
                    refreshToken,
                    "",
                    card.name,
                    card.description,
                    card.location,
                    card.age,
                    card.sex)
            } else {
                null
            }
        } else {
            call.respond(HttpStatusCode.Conflict, "Incorrect login or password")
            null
        }
    }


}


























