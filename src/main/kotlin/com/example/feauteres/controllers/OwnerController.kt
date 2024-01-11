package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

class OwnerController() {
    val secretController = SecretController()
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

    suspend fun registerOwner(call: ApplicationCall): PrivateOwnerResponse? {
        var registerIntent = call.receive<OwnerRegisterReceiveRemote>()
        val ownerModel = OwnerModel.fetch(email = registerIntent.email)
        val timestamp = java.util.Date().time

        if (ownerModel != null) {
            call.respond(HttpStatusCode.Conflict, "User already exist")
            return null
        } else {
            val idForOwner = UUID.randomUUID()
            val idForCard = UUID.randomUUID()
            val tokenController = RefreshTokenController(registerIntent.email, idForOwner)

            val cardDTO = CardDTO(
                id = idForCard,
                name = registerIntent.name,
                description = registerIntent.description,
                location = registerIntent.location,
                age = registerIntent.age,
                sex = registerIntent.sex,
                createdAt = timestamp,
                lastAuth = timestamp
            )

            val passwordHash = secretController.generateHash(registerIntent.password, timestamp.toString())
            val newOwnerDTO = OwnerDTO(
                id = idForOwner,
                email = registerIntent.email,
                password = passwordHash,
                location = registerIntent.location,
                cardID = idForCard,
                createdAt = timestamp
            )

            CardModel.create(cardDTO)
            OwnerModel.create(newOwnerDTO)
            val refreshToken = tokenController.createRefreshToken()

            return PrivateOwnerResponse(
                id = idForOwner.toString(),
                cardID = idForCard.toString(),
                refreshToken = refreshToken,
                accessToken = "",
                email = registerIntent.email,
                location = registerIntent.location,
                name = registerIntent.name,
                description = registerIntent.description,
                age = registerIntent.age,
                sex = registerIntent.sex
            )
        }

    }

    suspend fun authorisationOwnerWithRT(call: ApplicationCall): PrivateOwnerResponse? {
        println("NewOwnerController authorisationOwnerWithRT() START")

        val authCall = call.receive<OwnerAuthReceiveRemote>()
        val refreshTokenDTO = RefreshTokenModel.fetch(UUID.fromString(authCall.ownerID))

        if (refreshTokenDTO != null) {
            if (authCall.refreshToken.toInt() == refreshTokenDTO.token.toInt()) {
                val ownerDTO = OwnerModel.fetch(UUID.fromString(authCall.ownerID))
                if (ownerDTO != null) {
                    val card = CardModel.fetch(ownerDTO.cardID)
                    if (card != null) {
                        val refreshTokenController = RefreshTokenController(ownerDTO.email, ownerDTO.id)

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
                        call.respond(HttpStatusCode.Conflict, "Card not exist")
                        return null
                    }
                }
            }
            call.respond(HttpStatusCode.Conflict, "not valid token or owner not exist")
            return null
        } else {
            call.respond(HttpStatusCode.Conflict, "invalid token")
            return null
        }
    }

    suspend fun loginOwner(call: ApplicationCall): PrivateOwnerResponse? {
        println("NewOwnerController loginOwner() START")

        val loginReceiveRemote = call.receive<OwnerLoginReceiveRemote>()
        val ownerDTO = OwnerModel.fetch(email = loginReceiveRemote.email)

        println("NewOwnerController loginOwner() ${ownerDTO!!.email} ")

        return if (ownerDTO != null) {
            val passwordHash = secretController.generateHash(loginReceiveRemote.password, ownerDTO.createdAt.toString())

            return if (passwordHash == ownerDTO.password) {
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
                    call.respond(HttpStatusCode.Conflict, "Owner card not exist")
                    null
                }
            } else {
                call.respond(HttpStatusCode.Conflict, "Incorrect login or password")
                null
            }

        } else {
            call.respond(HttpStatusCode.Conflict, "Incorrect login or password")
            null
        }
    }


}


























