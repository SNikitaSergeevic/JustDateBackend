package com.example.feauteres.model


import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class OwnerAuthResponse (val ownerid: String,
                              val userpulicid: String,
                              var refreshToken: String,
                              val name: String,
                              val description: String,
                              val location: String,
                              val age: Int,
                              val sex: String,
                              var accessToken: String)

class OwnerDTO (val id: UUID, val email: String, val password: String, val location: String, val userpublicid: UUID)

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
    fun fetch(email: String): OwnerDTO? {
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

    fun fetch(id: UUID): OwnerDTO? {
        return try {
            transaction {
                val ownerModel = OwnerModel.select { OwnerModel.id.eq(id) }.single()
                OwnerDTO(
                    id = ownerModel[OwnerModel.id],
                    email = ownerModel[OwnerModel.email],
                    password = ownerModel[OwnerModel.password],
                    location = ownerModel[OwnerModel.location],
                    userpublicid = ownerModel[OwnerModel.userpublicid]
                )
            }
        } catch (e: Exception) {
            null
        }
    }



    fun update(owner: OwnerDTO) {
        println("updateOwner START")
        try {
            transaction {
                OwnerModel.update({OwnerModel.id eq owner.id}) {
                    it[id] = owner.id
                    it[email] = owner.email
                    it[password] = owner.password
                    it[location] = owner.location
                    it[userpublicid] = owner.userpublicid
                }
            }
        } catch (e: Exception) {
            print(e)
        }
    }

    fun delete(id: UUID) {
        println("deleteOwner START")
        try {

            transaction {
                OwnerModel.deleteWhere { OwnerModel.id eq id }
            }

        } catch (e: Exception) {

        }
    }


}


















