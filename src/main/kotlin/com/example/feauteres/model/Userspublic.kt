package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate


class UserspublicDTO(val id: UUID,
                     val name: String,
                     val description: String,
                     val location: String,
                     val age: Int,
                     val sex: String)

object UserspublicModel: Table("justdate_schema.userspublic") {
    private val id = UserspublicModel.uuid("id")
    private val name = UserspublicModel.varchar("name", 255)
    private val description = UserspublicModel.varchar("description", 255)
    private val location = UserspublicModel.varchar("location", 255)
    private val age = UserspublicModel.integer("age")
    private val sex = UserspublicModel.varchar("sex", 255)

    fun insert(userspublicDTO: UserspublicDTO) {
        transaction {
            UserspublicModel.insert{
                it[id] = userspublicDTO.id
                it[name] = userspublicDTO.name
                it[description] = userspublicDTO.description
                it[location] = userspublicDTO.location
                it[age] = userspublicDTO.age
                it[sex] = userspublicDTO.sex
            }
        }
    }

    fun fetch(id: String): UserspublicDTO? {
        return try {
            transaction {
                val userspublicModel = UserspublicModel.select {UserspublicModel.id.eq(UUID.fromString(id))}.single()
                UserspublicDTO(id = userspublicModel[UserspublicModel.id],
                    name = userspublicModel[UserspublicModel.name],
                    description = userspublicModel[UserspublicModel.description],
                    location = userspublicModel[UserspublicModel.location],
                    age = userspublicModel[UserspublicModel.age],
                    sex = userspublicModel[UserspublicModel.sex])
            }
        } catch(e: Exception) {
            null
        }
    }

    fun fetchOnSex(sex: String): List<UserspublicDTO>? {
        return try {
            transaction {
                val userpublicModel = UserspublicModel.select {UserspublicModel.sex.eq(sex)}
                userpublicModel.map{
                    UserspublicDTO(id = it[UserspublicModel.id],
                    name = it[UserspublicModel.name],
                    description = it[UserspublicModel.description],
                    location = it[UserspublicModel.location],
                    age = it[UserspublicModel.age],
                    sex = it[UserspublicModel.sex])}
            }
        } catch(e: Exception) {
            null
        }
    }

    fun update(user: UserspublicDTO) {
        print("userspublic UPDATE")
        try {
            transaction {
                UserspublicModel.update({UserspublicModel.id eq user.id}) {
                    it[name] = user.name
                    it[description] = user.description
                    it[location] = user.location
                    it[age] = user.age
                    it[sex] = user.sex
                }
            }
        } catch(e: Exception) {
            print(e)
        }
    }

    fun delete(id: UUID) {
        try {
            transaction{
                UserspublicModel.deleteWhere { UserspublicModel.id eq id}
            }
        } catch (e: Exception) {

        }
    }

}



























