package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date

import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.UUID

@Serializable
data class Token(val id: String,
                 val ownerid: String,
                 val token: String)

class TokenDTO(val id: UUID,
               val ownerid: UUID,
               val token: String)

object TokenModel: Table("justdate_schema.token") {
    private val id = TokenModel.uuid("id")
    private val ownerid = TokenModel.uuid("ownerid")
    private val token = TokenModel.varchar("token", 255)

    fun insert(tokenDTO: TokenDTO) {
        println("insert token START")
        transaction {
            TokenModel.insert {
                it[id] = tokenDTO.id
                it[ownerid] = tokenDTO.ownerid
                it[token] = tokenDTO.token
            }
        }
    }

    fun fetchToken(ownerid: UUID): TokenDTO? {
        println("fetchToken START")
        return try {
            transaction {
                val tokenModel = TokenModel.select {TokenModel.ownerid.eq(ownerid)}.single()
                TokenDTO(id = tokenModel[TokenModel.id],
                    ownerid = tokenModel[TokenModel.ownerid],
                    token = tokenModel[TokenModel.token])
            }
        } catch (e: Exception) {
            null
        }
    }

    fun deleteToken(ownerid: UUID) {
        println("deleteToken START")
        try {
            transaction {
                val del = TokenModel.deleteWhere {TokenModel.ownerid.eq(ownerid)}
                println("TOKEN DELETED")
            }
        } catch (e: Exception) {
            println("EXCEPTION $e")
        }
    }

}
