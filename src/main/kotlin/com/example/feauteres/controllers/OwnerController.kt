package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.time.LocalDate
import java.util.*

class OwnerController(private val call: ApplicationCall) {

    suspend fun updateOwner() {
        println("NewOwnerController updateOwner() START")

        var updateRemote = call.receive<UpdateOwnerRemote>()

        OwnerModel.update(
            OwnerDTO(id = UUID.fromString(updateRemote.id, ),
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

    suspend fun fetchOwner(): FetchOwnerRespond? {
        println("NewOwnerController fetchOwner() START")

        val id = call.receive<FetchOwnerRemote>().id
        val owner = OwnerModel.fetch(UUID.fromString(id))

        return if (owner != null) {
            val token = RefreshTokenModel.fetch(owner.id)
            return if (token != null) {
                FetchOwnerRespond(
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
            val deleteRemote = call.receive<DeleteOwnerRemote>()
            RefreshTokenModel.deleteToken(UUID.fromString(deleteRemote.id))
            OwnerModel.delete(UUID.fromString(deleteRemote.id))
            CardModel.delete(UUID.fromString(deleteRemote.cardID))
            call.respond(HttpStatusCode.OK, "Owner deleted")
        } catch (e: Exception) {

        }
    }

    suspend fun registerOwner(): OwnerRegisterResponseRemote? {
        var register = call.receive<OwnerRegisterReceiveRemote>()
        val ownerModel = OwnerModel.fetch(email = register.email)

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

            val newOwnerDTO = OwnerDTO(
                id = idForOwner,
                email = register.email,
                password = register.password,
                location = register.location,
                cardID = idForCard,
                createdAt = LocalDate.now()
            )

            CardModel.create(cardDTO)
            OwnerModel.create(newOwnerDTO)
            val refreshToken = tokenController.createRefreshToken()
            return OwnerRegisterResponseRemote(idForOwner.toString(), idForCard.toString(), refreshToken)
        }

    }


    suspend fun authorisationOwnerWithRT(): OwnerAuthResponse? {
        println("NewOwnerController authorisationOwnerWithRT() START")

        val authCall = call.receive<OwnerAuthReceiveRemote>()
        val refreshTokenDTO = RefreshTokenModel.fetch(UUID.fromString(authCall.ownerID))

        if (refreshTokenDTO != null) {
            if (authCall.refreshToken.toInt() == refreshTokenDTO.token.toInt()) {
                val ownerDTO = OwnerModel.fetch(UUID.fromString(authCall.ownerID))
                if (ownerDTO != null) {
                    val card = CardModel.fetch(ownerDTO.cardID.toString())
                    if (card != null) {
                        val refreshTokenController = RefreshTokenController(ownerDTO!!.email, ownerDTO!!.id)
                        if (refreshTokenController.checkRefreshToken()) refreshTokenController.deleteRefreshToken() else return null
                        val refreshToken = refreshTokenController.createRefreshToken()
                        return OwnerAuthResponse(ownerDTO.id.toString(),
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

    suspend fun loginOwner(): OwnerAuthResponse? {
        println("NewOwnerController loginOwner() START")

        val loginReceiveRemote = call.receive<OwnerLoginReceiveRemote>()
        val ownerDTO = OwnerModel.fetch(email = loginReceiveRemote.email)

        println("NewOwnerController loginOwner() ${ownerDTO!!.email} ")

        return if (ownerDTO != null && ownerDTO.password == loginReceiveRemote.password) {
            val card = CardModel.fetch(ownerDTO.cardID.toString())
            if (card != null) {
                val tokenController = RefreshTokenController(loginReceiveRemote.email, ownerDTO.id)
                tokenController.deleteRefreshToken()
                val refreshToken = tokenController.createRefreshToken()
                OwnerAuthResponse(ownerDTO.id.toString(),
                    ownerDTO.cardID.toString(),
                    refreshToken,
                    "",
                    card.name,
                    card.description,
                    card.location,
                    card.age,
                    card.sex)
            } else {
                println("NewOwnerController loginOwner() card is null - ${card == null} ")
                null
            }
        } else {
            call.respond(HttpStatusCode.Conflict, "Incorrect login or password")
            null
        }
    }


}


























