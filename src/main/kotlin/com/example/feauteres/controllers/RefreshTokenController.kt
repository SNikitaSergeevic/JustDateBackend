package com.example.feauteres.controllers

import com.example.feauteres.model.RefreshTokenDTO
import com.example.feauteres.model.RefreshTokenModel
import java.time.LocalDate
import java.util.*
import kotlin.random.Random




class RefreshTokenController(private val email: String, private val ownerID: UUID) {
    fun createRefreshToken(): String {
        val refreshToken = (Random.nextInt(1000).toString() + email).hashCode().toString()
        val tokenID: UUID = UUID.randomUUID()
        val token = RefreshTokenDTO(id = tokenID, ownerID = ownerID, token = refreshToken, createdAt = java.util.Date().time)
        RefreshTokenModel.create(token)
        return refreshToken
    }

    fun deleteRefreshToken() {
        RefreshTokenModel.deleteToken(ownerID)
    }

    fun checkRefreshToken(): Boolean {
        val tok = RefreshTokenModel.fetch(ownerID)
        return (tok != null)
    }

}














