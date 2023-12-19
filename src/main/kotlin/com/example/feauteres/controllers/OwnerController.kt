package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.time.LocalDate
import java.util.*

class OwnerController() {

    suspend fun updateOwner(localCall: ApplicationCall) {
        println("NewOwnerController updateOwner() START")

        var updateRemote = localCall.receive<UpdateOwnerReceiveRemote>()
        val currentOwner = OwnerModel.fetch(UUID.fromString(updateRemote.id))

        val now = java.util.Date().time

        if (currentOwner != null) {
            val currentCard = CardModel.fetch(currentOwner.cardID)
            OwnerModel.update(
                owner = OwnerDTO(id = UUID.fromString(updateRemote.id),
                email = updateRemote.email,
                password = "",
                location = updateRemote.location,
                cardID = UUID.fromString(updateRemote.cardID),
                createdAt = currentOwner.createdAt)
            )
            if (currentCard != null) {
                CardModel.update(
                    CardDTO(id = UUID.fromString(updateRemote.cardID),
                    name = updateRemote.name,
                    description = updateRemote.description,
                    location = updateRemote.location,
                    age = updateRemote.age,
                    sex = updateRemote.sex,
                    createdAt = currentCard.lastAuth,
                    lastAuth =  now)
                )
            }
            localCall.respond(HttpStatusCode.OK, "Owner updated")
        } else {
            localCall.respond(HttpStatusCode.NotFound)
        }
    }

    suspend fun fetchPublicOwner(localCall: ApplicationCall): PublicOwnerResponse? {
        println("NewOwnerController fetchPublicOwner() START")

        val id = localCall.parameters["ownerID"]
        val ownerDTO = OwnerModel.fetch(UUID.fromString(id))

        return if (ownerDTO != null) {
            val cardDTO = CardModel.fetch(ownerDTO.cardID)
            val tokenDTO = RefreshTokenModel.fetch(ownerDTO.id)
            return if (tokenDTO != null && cardDTO != null) {
                PublicOwnerResponse(
                    id = ownerDTO.id.toString(),
                    cardID = cardDTO.id.toString(),
                    location = ownerDTO.location,
                    name = cardDTO.name,
                    description = cardDTO.description,
                    age = cardDTO.age,
                    sex = cardDTO.sex
                )
            } else {
                null
            }
        } else {
            null
        }
    }

    suspend fun fetchPrivateOwner(localCall: ApplicationCall): PrivateOwnerResponse? {
        println("OwnerController fetchPrivateOwner() START")

        val ownerReceive = localCall.receive<PrivateOwnerReceiveRemote>()
        val owner = OwnerModel.fetch(UUID.fromString(ownerReceive.ownerID))

        return if (owner != null && ownerReceive.email == owner.email) {
            val token = RefreshTokenModel.fetch(owner.id)
            val card = CardModel.fetch(owner.cardID)
            if (token != null && token.token == ownerReceive.refreshToken && card != null) {
                PrivateOwnerResponse(
                    id = owner.id.toString(),
                    cardID = card.id.toString(),
                    refreshToken = token.token,
                    accessToken = "",
                    email = owner.email,
                    location = owner.location,
                    name = card.name,
                    description = card.description,
                    age = card.age,
                    sex = card.sex

                )
            } else {
                null
            }
        } else {
            null
        }

    }

    suspend fun deleteOwner(localCall: ApplicationCall) {
        println("NewOwnerController deleteOwner() START")

        try {
            val deleteRemote = localCall.receive<DeleteOwnerReceiveRemote>()
            RefreshTokenModel.deleteToken(UUID.fromString(deleteRemote.id))
            OwnerModel.delete(UUID.fromString(deleteRemote.id))
            CardModel.delete(UUID.fromString(deleteRemote.cardID))
            localCall.respond(HttpStatusCode.OK, "Owner deleted")
        } catch (e: Exception) {

        }
    }

    suspend fun registerOwner(localCall: ApplicationCall): PrivateOwnerResponse? {
        var register = localCall.receive<OwnerRegisterReceiveRemote>()
        val ownerModel = OwnerModel.fetch(email = register.email)
        val now = java.util.Date().time

        if (ownerModel != null) {
            localCall.respond(HttpStatusCode.Conflict, "User already exist")
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
                createdAt = now,
                lastAuth = now
            )

            val newOwnerDTO = OwnerDTO(
                id = idForOwner,
                email = register.email,
                password = register.password,
                location = register.location,
                cardID = idForCard,
                createdAt = now
            )

            CardModel.create(cardDTO)
            OwnerModel.create(newOwnerDTO)
            val refreshToken = tokenController.createRefreshToken()

            return PrivateOwnerResponse(
                id = idForOwner.toString(),
                cardID = idForCard.toString(),
                refreshToken = refreshToken,
                accessToken = "",
                email = register.email,
                location = register.location,
                name = register.name,
                description = register.description,
                age = register.age,
                sex = register.sex
            )
        }

    }


    suspend fun authorisationOwnerWithRT(localCall: ApplicationCall): PrivateOwnerResponse? {
        println("NewOwnerController authorisationOwnerWithRT() START")

        val authCall = localCall.receive<OwnerAuthReceiveRemote>()
        val refreshTokenDTO = RefreshTokenModel.fetch(UUID.fromString(authCall.ownerID))

        if (refreshTokenDTO != null) {
            if (authCall.refreshToken.toInt() == refreshTokenDTO.token.toInt()) {
                val ownerDTO = OwnerModel.fetch(UUID.fromString(authCall.ownerID))
                if (ownerDTO != null) {
                    val card = CardModel.fetch(ownerDTO.cardID)
                    if (card != null) {
                        val refreshTokenController = RefreshTokenController(ownerDTO!!.email, ownerDTO!!.id)
                        if (refreshTokenController.checkRefreshToken()) refreshTokenController.deleteRefreshToken() else return null
                        val refreshToken = refreshTokenController.createRefreshToken()
                        return PrivateOwnerResponse(ownerDTO.id.toString(),
                            card.id.toString(),
                            refreshToken,
                            "",
                            ownerDTO.email,
                            card.location,
                            card.name,
                            card.description,
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

    suspend fun loginOwner(localCall: ApplicationCall): PrivateOwnerResponse? {
        println("NewOwnerController loginOwner() START")

        val loginReceiveRemote = localCall.receive<OwnerLoginReceiveRemote>()
        val ownerDTO = OwnerModel.fetch(email = loginReceiveRemote.email)

        println("NewOwnerController loginOwner() ${ownerDTO!!.email} ")

        return if (ownerDTO != null && ownerDTO.password == loginReceiveRemote.password) {
            val card = CardModel.fetch(ownerDTO.cardID)
            if (card != null) {
                val tokenController = RefreshTokenController(loginReceiveRemote.email, ownerDTO.id)
                tokenController.deleteRefreshToken()
                val refreshToken = tokenController.createRefreshToken()
                PrivateOwnerResponse(ownerDTO.id.toString(),
                    ownerDTO.cardID.toString(),
                    refreshToken,
                    "",
                    ownerDTO.email,
                    card.location,
                    card.name,
                    card.description,
                    card.age,
                    card.sex)
            } else {
                println("NewOwnerController loginOwner() card is null")
                null
            }
        } else {
            localCall.respond(HttpStatusCode.Conflict, "Incorrect login or password")
            null
        }
    }


}


























