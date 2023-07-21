package com.example.feauteres.model


import kotlinx.serialization.Serializable
import org.h2.table.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class Owner(val id: String, val email: String, val password: String, val location: String, val userpublicid: String)

class OwnerDTO ( val id: UUID, val email: String, val password: String, val location: String, val userpublicid: UUID)

object OwnerModel: Table("justdate_schema.owner") {
    private val id = OwnerModel.uuid("id")
    private val email = OwnerModel.varchar("email", 255)
    private val password = OwnerModel.varchar("password", 255)
    private val location = OwnerModel.varchar("location", 255)
    private val userpublicid = OwnerModel.uuid("userpublicid")

    fun insert(ownerDTO: OwnerDTO) {
        transaction {
            OwnerModel.insert {
                it[id] = ownerDTO.id
                it[email] = ownerDTO.email
                it[password] = ownerDTO.password
                it[location] = ownerDTO.location
                it[userpublicid] = ownerDTO.userpublicid
            }
        }
    }

    //FIXME: generics this fetchOwners
    fun fetchOwner(email: String): OwnerDTO? {
        return try {
            transaction {
                val ownerModel = OwnerModel.select { OwnerModel.email.eq(email) }.single()
                OwnerDTO (id = ownerModel[OwnerModel.id],
                    email = ownerModel[OwnerModel.email],
                    password = ownerModel[OwnerModel.password],
                    location = ownerModel[OwnerModel.location],
                    userpublicid = ownerModel[OwnerModel.userpublicid])
            }
        } catch (e: Exception){
            null
        }
    }

    fun fetchOwner(id: UUID): OwnerDTO? {
        return try {
            transaction {
                val ownerModel = OwnerModel.select {OwnerModel.id.eq(id)}.single()
                OwnerDTO (id = ownerModel[OwnerModel.id],
                    email = ownerModel[OwnerModel.email],
                    password = ownerModel[OwnerModel.password],
                    location = ownerModel[OwnerModel.location],
                    userpublicid = ownerModel[OwnerModel.userpublicid])
            }
        } catch (e: Exception) {
            null
        }
    }
}