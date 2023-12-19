package com.example.feauteres.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate
import org.jetbrains.exposed.sql.*

import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


@Serializable
data class RefreshToken(val id: String,
                        val ownerID: String,
                        val token: String,
                        val createdAt: Long)

class RefreshTokenDTO(val id: UUID,
                      val ownerID: UUID,
                      val token: String,
                      val createdAt: Long)

object RefreshTokenModel: Table("refresh_token") {
    private val id: Column<UUID> = RefreshTokenModel.uuid("id")
    private val ownerID: Column<UUID> = RefreshTokenModel.uuid("owner_id")
    private val token: Column<String> = RefreshTokenModel.varchar("token", 255)
    private val createdAt: Column<Long> = RefreshTokenModel.long("created_at")

    fun create(refreshToken: RefreshTokenDTO) {
        println("RefreshTokenModel create(token: TokenDTO) START")

        transaction {
            RefreshTokenModel.insert {
                it[id] = refreshToken.id
                it[ownerID] = refreshToken.ownerID
                it[token] = refreshToken.token
                it[createdAt] = refreshToken.createdAt
            }
        }
    }

    fun fetch(ownerID: UUID): RefreshTokenDTO? {
        println("RefreshTokenModel fetchToken(ownerID: UUID) START")

        return try {
            transaction {
                val tokenModel = RefreshTokenModel.select{ RefreshTokenModel.ownerID.eq(ownerID)}.single()
                RefreshTokenDTO(id = tokenModel[RefreshTokenModel.id],
                    ownerID = tokenModel[RefreshTokenModel.ownerID],
                    token = tokenModel[token],
                    createdAt = tokenModel[createdAt])
            }
        } catch (e: Exception) {
            null
        }

    }

    fun deleteToken(ownerID: UUID) {
        println("RefreshTokenModel deleteToken(ownerID: UUID) START")
        try {
            transaction {
                RefreshTokenModel.deleteWhere { RefreshTokenModel.ownerID.eq(ownerID)}
            }
        } catch (e: Exception) {
            println(e)
        }
    }

}