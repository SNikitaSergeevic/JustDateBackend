package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class ImagesResponse (val id: String,
                           val path: String,
                           val userid: String,
                           val filename: String)

class ImagesDTO (val id: UUID, val path: String, val userid: UUID, val filename: String)

object ImagesModel: Table("justdate_schema.images") {
    private val id = ImagesModel.uuid("id")
    private val path = ImagesModel.varchar("path", 255)
    private val userid = ImagesModel.uuid("userid")
    private val filename = ImagesModel.varchar("filename", 255)

    fun insert(imagesDTO: ImagesDTO) {
        println("insertImages START")
        transaction {
            ImagesModel.insert {
                it[id] = imagesDTO.id
                it[path] = imagesDTO.path
                it[userid] = imagesDTO.userid
                it[filename] = imagesDTO.filename
            }
        }
    }

    fun fetch(id: UUID): ImagesDTO? {
        return try {
            transaction {
                val imagesModel = ImagesModel.select {ImagesModel.id.eq(id)}.single()
                ImagesDTO(id = imagesModel[ImagesModel.id],
                    path = imagesModel[ImagesModel.path],
                    userid = imagesModel[ImagesModel.userid],
                    filename = imagesModel[ImagesModel.filename])
            }
        } catch (e: Exception) {
            null
        }
    }

    fun update(image: ImagesDTO) {
        println("updateImages START")
        try {
            transaction {
                ImagesModel.update({ImagesModel.id eq image.id}) {
                    it[path] = image.path
                    it[filename] = image.filename
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    fun delete(id: UUID) {
        println("deleteImages START")
        try {
            transaction {
                ImagesModel.deleteWhere {ImagesModel.id eq id}
            }
        } catch (e: Exception) {
            println(e)
        }
    }



}


