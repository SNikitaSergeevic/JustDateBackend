package com.example.feauteres.model.news

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.*


@Serializable
data class UpdateNewOwnerRemote(val id: String,
                                val email: String,
                                val password: String,
                                val location: String,
                                val cardID: String,
                                val name: String,
                                val description: String,
                                val age: Int,
                                val sex: String)

@Serializable
data class NewOwnerAuthResponse(val id: String,
                                val cardID: String,
                                val refreshToken: String,
                                val accessToken: String,
                                val name: String,
                                val description: String,
                                val location: String,
                                val age: Int,
                                val sex: String)

@Serializable
data class NewOwnerAuthReceiveRemote(val ownerID: String,
                                     val refreshToken: String)

@Serializable
data class NewOwnerLoginReceiveRemote(val email: String,
                                      val password: String)

@Serializable
data class FetchNewOwnerRespond(val id: String,
                                val email: String,
                                val password: String,
                                val location: String,
                                val cardID: String,
                                val refreshToken: String)

@Serializable
data class DeleteNewOwnerRemote(val id: String,
                                val cardID: String)

@Serializable
data class FetchNewOwnerRemote(val id: String)

@Serializable
data class NewOwnerRegisterReceiveRemote(val email: String,
                                         val password: String,
                                         val name: String,
                                         val location: String,
                                         val description: String,
                                         val age: Int,
                                         val sex: String)

@Serializable
data class NewOwnerRegisterResponseRemote(val ownerID: String,
                                          val cardID: String,
                                          val refreshToken: String)

class NewOwnerDTO(val id: UUID,
                  val email: String,
                  val password: String,
                  val location: String,
                  val cardID: UUID,
                  val createdAt: LocalDate
)

object NewOwnerModel: Table("justdatedb_1.owner") {
    private val id: Column<UUID> = NewOwnerModel.uuid("id")
    private val email: Column<String> = NewOwnerModel.varchar("email", 255)
    private val password: Column<String> = NewOwnerModel.varchar("password", 255)
    private val location: Column<String> = NewOwnerModel.varchar("location", 255)
    private val cardID: Column<UUID> = NewOwnerModel.uuid("card_id")
    private val createdAt: Column<LocalDate> = NewOwnerModel.date("created_at")

    fun create(owner: NewOwnerDTO) {
        transaction {
            NewOwnerModel.insert {
                it[id] = owner.id
                it[email] = owner.email
                it[password] = owner.password
                it[location] = owner.location
                it[cardID] = owner.cardID
                it[createdAt] = owner.createdAt
            }
        }
    }

    fun fetch(email: String): NewOwnerDTO? {
        println("NewOwnerModel fetch(email: String) START")
        return try {
            transaction {
                val ownerModel = NewOwnerModel.select { NewOwnerModel.email.eq(email)}.single()
                NewOwnerDTO(id = ownerModel[NewOwnerModel.id],
                    email = ownerModel[NewOwnerModel.email],
                    password = ownerModel[password],
                    location = ownerModel[location],
                    cardID = ownerModel[cardID],
                    createdAt = ownerModel[createdAt])
            }
        } catch(e: Exception) {
            null
        }
    }

    fun fetch(id: UUID): NewOwnerDTO? {
        println("NewOwnerModel fetch(id: UUID) START")
        return try {
            transaction {
                val ownerModel = NewOwnerModel.select { NewOwnerModel.id.eq(id)}.single()
                NewOwnerDTO(id = ownerModel[NewOwnerModel.id],
                    email = ownerModel[email],
                    password = ownerModel[password],
                    location = ownerModel[location],
                    cardID = ownerModel[cardID],
                    createdAt = ownerModel[createdAt])
            }
        } catch(e: Exception) {
            null
        }
    }

    fun update(owner: NewOwnerDTO) {
        println("NewOwnerModel update(owner: OwnerDTO)")
        try {
            transaction {
                NewOwnerModel.update({ NewOwnerModel.id eq owner.id}) {
                    it[id] = owner.id
                    it[email] = owner.email
                    it[password] = owner.password
                    it[location] = owner.location
                    it[cardID] = owner.cardID
                    it[createdAt] = owner.createdAt
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
                NewOwnerModel.deleteWhere { NewOwnerModel.id eq id}
            }
        } catch(e: Exception) {
            println(e)
        }
    }


}