package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.*




//TODO: Owner RECEIVE
@Serializable
data class OwnerAuthReceiveRemote(
    val ownerID: String,
    val refreshToken: String
)

@Serializable
data class OwnerLoginReceiveRemote(
    val email: String,
    val password: String
)

@Serializable
data class OwnerRegisterReceiveRemote(
    val email: String,
    val password: String,
    val name: String,
    val location: String,
    val description: String,
    val age: Int,
    val sex: String
)

@Serializable
data class DeleteOwnerReceiveRemote(
    val id: String,
    val cardID: String
)

@Serializable
data class PrivateOwnerReceiveRemote(
    val ownerID: String,
    val refreshToken: String,
    val email: String,
    val cardID: String
)

@Serializable
data class UpdateOwnerReceiveRemote(
    val id: String,
    val cardID: String,
    val email: String,
    val location: String,
    val name: String,
    val description: String,
    val age: Int,
    val sex: String
)

//TODO: Public owner RESPONSE
@Serializable
data class PublicOwnerResponse(
    val id: String,
    val cardID: String,
    val location: String,
    val name: String,
    val description: String,
    val age: Int,
    val sex: String
)

//TODO: Private owner RESPONSE
@Serializable
data class PrivateOwnerResponse(
    val id: String,
    val cardID: String,
    val refreshToken: String,
    var accessToken: String,
    val email: String,
    val location: String,
    val name: String,
    val description: String,
    val age: Int,
    val sex: String
)

class OwnerDTO(
    val id: UUID,
    val email: String,
    val password: String,
    val location: String,
    val cardID: UUID,
    val createdAt: LocalDate
)

object OwnerModel : Table("owner") {
    private val id: Column<UUID> = OwnerModel.uuid("id")
    private val email: Column<String> = OwnerModel.varchar("email", 255)
    private val password: Column<String> = OwnerModel.varchar("password", 255)
    private val location: Column<String> = OwnerModel.varchar("location", 255)
    private val cardID: Column<UUID> = OwnerModel.uuid("card_id")
    private val createdAt: Column<LocalDate> = OwnerModel.date("created_at")

    fun create(owner: OwnerDTO) {
        transaction {
            OwnerModel.insert {
                it[id] = owner.id
                it[email] = owner.email
                it[password] = owner.password
                it[location] = owner.location
                it[cardID] = owner.cardID
                it[createdAt] = owner.createdAt
            }
        }
    }

    fun fetch(email: String): OwnerDTO? {
        println("NewOwnerModel fetch(email: String) START")
        return try {
            transaction {
                val ownerModel = OwnerModel.select { OwnerModel.email.eq(email) }.single()
                OwnerDTO(
                    id = ownerModel[OwnerModel.id],
                    email = ownerModel[OwnerModel.email],
                    password = ownerModel[password],
                    location = ownerModel[location],
                    cardID = ownerModel[cardID],
                    createdAt = ownerModel[createdAt]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetch(id: UUID): OwnerDTO? {
        println("NewOwnerModel fetch(id: UUID) START")
        return try {
            transaction {
                val ownerModel = OwnerModel.select { OwnerModel.id.eq(id) }.single()
                OwnerDTO(
                    id = ownerModel[OwnerModel.id],
                    email = ownerModel[email],
                    password = ownerModel[password],
                    location = ownerModel[location],
                    cardID = ownerModel[cardID],
                    createdAt = ownerModel[createdAt]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchOnCradid(cardID: UUID): OwnerDTO? {
        println("NewOwnerModel fetchOnCradid(cardID: UUID) START")
        return try {
            transaction {
                val ownerModel = OwnerModel.select { OwnerModel.cardID.eq(cardID) }.single()
                OwnerDTO(
                    id = ownerModel[OwnerModel.id],
                    email = ownerModel[email],
                    password = ownerModel[password],
                    location = ownerModel[location],
                    cardID = ownerModel[OwnerModel.cardID],
                    createdAt = ownerModel[createdAt]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun update(owner: OwnerDTO) {
        println("NewOwnerModel update(owner: OwnerDTO)")
        try {
            transaction {
                OwnerModel.update({ OwnerModel.id eq owner.id }) {
                    it[id] = owner.id
                    it[email] = owner.email
                    it[location] = owner.location
                    it[cardID] = owner.cardID
                }
            }
        } catch (e: Exception) {
            println(e)
        }

    }

    fun delete(id: UUID) {
        println("NewOwnerModel delete(owner: OwnerDTO)")
        try {
            transaction {
                OwnerModel.deleteWhere { OwnerModel.id eq id }
            }
        } catch (e: Exception) {
            println(e)
        }
    }


}