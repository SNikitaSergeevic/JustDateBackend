package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class Match (val id: String,
                  val ownerid: String,
                  val likeduserid: String,
                  val matched: Boolean)

class MatchDTO (val id: UUID, val ownerid: UUID, val likeduserid: UUID, val matched: Boolean)

object MatchModel: Table("justdate_schema.owner") {
    private val id = MatchModel.uuid("id")
    private val ownerid = MatchModel.uuid("ownerid")
    private val likeduserid = MatchModel.uuid("likeduserid")
    private val matched = MatchModel.bool("matched")

    fun insert(matchDTO: MatchDTO) {
        println("insertMatch START")
        transaction {
            MatchModel.insert {
                it[id] = matchDTO.id
                it[ownerid] = matchDTO.ownerid
                it[likeduserid] = matchDTO.likeduserid
                it[matched] = matchDTO.matched
            }
        }
    }

    fun fetch(id: UUID): MatchDTO? {
        println("fetchMatch START")
        return try {
            transaction {
                val matchModel = MatchModel.select {MatchModel.id.eq(id)}.single()
                MatchDTO(id = matchModel[MatchModel.id],
                    ownerid = matchModel[MatchModel.ownerid],
                    likeduserid = matchModel[MatchModel.likeduserid],
                    matched = matchModel[MatchModel.matched])
            }
        } catch (e: Exception) {
            null
        }
    }

    fun update(match: MatchDTO) {
        println("updateMatch  START")
        try {
            transaction {
                MatchModel.update({MatchModel.id eq match.id}) {
                    it[matched] = match.matched
                }
            }
        } catch (e: Exception) {

        }
    }

    fun delete(id: UUID) {
        println("deleteMatch START")
        try {
            transaction {
                MatchModel.deleteWhere {MatchModel.id eq id}
            }
        } catch(e: Exception) {


        }
    }

}