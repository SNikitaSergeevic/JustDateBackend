package com.example.feauteres.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.model.TokenDTO
import com.example.feauteres.model.TokenModel
import com.example.feauteres.register.RegisterController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

class TokenController(private val email: String, private val ownerid: UUID) {
    suspend fun createRefreshToken(): String {

        val refreshToken = email.hashCode().toString()

        val tokenid: UUID = UUID.randomUUID()

        val token = TokenDTO(id = tokenid, ownerid = ownerid, token = refreshToken)
        TokenModel.insert(token)

        return refreshToken
    }

    fun deleteRefreshToken() {
        TokenModel.deleteToken(ownerid)
    }

}








