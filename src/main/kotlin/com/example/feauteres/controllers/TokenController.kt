package com.example.feauteres.controllers

import com.example.feauteres.model.TokenDTO
import com.example.feauteres.model.TokenModel
import java.util.*
import kotlin.random.Random

class TokenController(private val email: String, private val ownerid: UUID) {
    suspend fun createRefreshToken(): String {

        val refreshToken = (Random.nextInt(1000).toString() + email).hashCode().toString()

        val tokenid: UUID = UUID.randomUUID()

        val token = TokenDTO(id = tokenid, ownerid = ownerid, token = refreshToken)
        TokenModel.insert(token)

        return refreshToken
    }

    fun deleteRefreshToken() {
        TokenModel.deleteToken(ownerid)
    }

    fun checkRefreshToken(): Boolean {
        val tok = TokenModel.fetchToken(ownerid)
        return (tok != null)
    }

}








