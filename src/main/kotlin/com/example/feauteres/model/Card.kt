package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate



@Serializable
data class FetchCardSexReceiveRemote(val sex: String)



@Serializable
data class CardRemote(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val age: Int,
    val sex: String,
    val imageIDs: List<ImageResponse>,
    val createdAt: String,
    val lastAuth: String
)

@Serializable
data class CardResponse(var cards: List<CardRemote>)

class CardDTO(val id: UUID,
              val name: String,
              val description: String,
              val location: String,
              val age: Int,
              val sex: String,
              val createdAt: LocalDate,
              val lastAuth: LocalDate)

object CardModel: Table("card") {
    private val id: Column<UUID> = CardModel.uuid("id")
    private val name: Column<String> = CardModel.varchar("name", 255)
    private val description: Column<String> = CardModel.text("description")
    private val location: Column<String> = CardModel.varchar("location", 255)
    private val age: Column<Int> = CardModel.integer("age")
    private val sex: Column<String> = CardModel.varchar("sex", 255)
    private val createdAt: Column<LocalDate> = CardModel.date("created_at")
    private val lastAuth: Column<LocalDate> = CardModel.date("last_auth")

    fun create(card: CardDTO) {
        println("CardModel create(card: CardDTO) START")
        transaction {
            CardModel.insert {
                it[id] = card.id
                it[name] = card.name
                it[description] = card.description
                it[location] = card.location
                it[age] = card.age
                it[sex] = card.sex
                it[createdAt] = card.createdAt
                it[lastAuth] = card.lastAuth
            }
        }
    }

    fun fetch(id: UUID): CardDTO? {
        println("CardModel fetch(id: String) START")
        return try {
            transaction {
                val cardModel = CardModel.select { CardModel.id.eq(id) }.single()
                CardDTO(
                    id = cardModel[CardModel.id], name = cardModel[name],
                    description = cardModel[description], location = cardModel[location],
                    age = cardModel[age], sex = cardModel[sex],
                    createdAt = cardModel[createdAt], lastAuth = cardModel[lastAuth]
                )
            }
        } catch (e: Exception) {
            println("CardModel fetch(id: String) Exception: ${e}")
            null
        }
    }

    fun fetchOnSex(sex: String): List<CardDTO>? {
        return try {
            transaction {
                val cardModel = CardModel.select { CardModel.sex.eq(sex)}.limit(10)
                println("cardModel.count is ${cardModel.count()}")
                cardModel.map{
                    CardDTO(id = it[CardModel.id], name = it[name],
                    description = it[description], location = it[location],
                    age = it[age], sex = it[CardModel.sex], createdAt = it[createdAt],
                    lastAuth = it[lastAuth])
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun update(card: CardDTO) {
        println("CardModel update(card: CardDTO) START")
        try {
            transaction {
                CardModel.update( { CardModel.id eq card.id } ) {
                    it[id] = card.id
                    it[name] = card.name
                    it[description] = card.description
                    it[location] = card.location
                    it[age] = card.age
                    it[sex] = card.sex
                }
            }
        } catch(e: Exception) {
            null
        }
    }

    fun delete(id: UUID) {
        println("CardModel delete() START")
        try {
            transaction {
                CardModel.deleteWhere { CardModel.id eq id }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

}





















