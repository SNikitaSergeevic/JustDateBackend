package com.example.feauteres.model.news

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.UUID

@Serializable
data class ImageResponse (val id: String,
                          val path: String,
                          val cardID: String,
                          val fileName: String,
                          val createdAt: String)

class ImageDTO (val id: UUID,
                val path: String,
                val cardID: UUID,
                val fileName: String,
                val createdAt: LocalDate)




object ImageModel: Table("image") {
    private val id: Column<UUID> = ImageModel.uuid("id")
    private val path: Column<String> = ImageModel.varchar("path", 255)
    private val cardID: Column<UUID> = ImageModel.uuid("card_id")
    private val fileName: Column<String> = ImageModel.varchar("file_name", 255)
    private val createdAt: Column<LocalDate> = ImageModel.date("created_at")

    fun create(imageDTO: ImageDTO) {
        transaction {
            ImageModel.insert {
                it[id] = imageDTO.id
                it[path] = imageDTO.path
                it[cardID] = imageDTO.cardID
                it[fileName] = imageDTO.fileName
                it[createdAt] = imageDTO.createdAt
            }
        }
    }

    fun fetch(imageID: UUID): ImageDTO? {
        return try {
            transaction {
                val imageModel = ImageModel.select { ImageModel.id.eq(imageID) }.single()
                ImageDTO(
                    id = imageModel[ImageModel.id],
                    path = imageModel[ImageModel.path],
                    cardID = imageModel[ImageModel.cardID],
                    fileName = imageModel[ImageModel.fileName],
                    createdAt = imageModel[ImageModel.createdAt]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchAllForCard(cardID: UUID): List<ImageDTO>? {
        return try {
            transaction {
                val imageModel = ImageModel.select { ImageModel.cardID.eq(cardID) }
                imageModel.map {ImageDTO(
                    id = it[ImageModel.id],
                    path = it[ImageModel.path],
                    cardID = it[ImageModel.cardID],
                    fileName = it[ImageModel.fileName],
                    createdAt = it[ImageModel.createdAt]
                ) }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun update(imageDTO: ImageDTO) {
        try {
            transaction {
                ImageModel.update({ImageModel.id eq imageDTO.id}) {
                    it[path] = imageDTO.path
                    it[fileName] = imageDTO.fileName
                }
            }
        } catch (e: Exception) {

        }
    }

    fun delete(id: UUID) {
        try {
            transaction {
                ImageModel.deleteWhere {ImageModel.id eq id}
            }
        } catch (e: Exception) {
            println(e)
        }
    }

}


































