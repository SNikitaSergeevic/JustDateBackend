package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Serializable
data class Userspublic(val id: String, val name: String, val description: String, val location: String, val age: Int, val sex: String)

class UserspublicDTO(val id: UUID, val name: String, val description: String, val location: String, val age: Int, val sex: String)

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
}