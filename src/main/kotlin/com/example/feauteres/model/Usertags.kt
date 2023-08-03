package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class UsertagsResponse (val id: String, val userid: String, val tagid: String)

class UsertagsDTO(val id: UUID, val userid: UUID, val tagid: UUID)

object UsertagsModel: Table("justdate_schema.usertags") {
    private val id = UsertagsModel.uuid("id")
    private val userid = UsertagsModel.uuid("userid")
    private val tagid = UsertagsModel.uuid("tagid")

    fun insert(usertagsDTO: UsertagsDTO) {
        println("insertUsertags  START ")
        transaction {
            UsertagsModel.insert {
                it[id] = usertagsDTO.id
                it[userid] = usertagsDTO.userid
                it[tagid] = usertagsDTO.tagid
            }
        }
    }

    fun fetch(id: UUID): UsertagsDTO? {
        println("fetchUsertags  START ")
        return try {
            transaction {
                val usertagsModel = UsertagsModel.select {UsertagsModel.id.eq(id)}.single()
                UsertagsDTO(id = usertagsModel[UsertagsModel.id],
                    userid = usertagsModel[UsertagsModel.userid],
                    tagid = usertagsModel[UsertagsModel.tagid])
            }
        } catch (e: Exception) {
            null
        }
    }

    fun delete(id: UUID) {
        println("fetchtUsertags  START ")
        transaction {
            UsertagsModel.deleteWhere {UsertagsModel.id eq id}
        }
    }


}