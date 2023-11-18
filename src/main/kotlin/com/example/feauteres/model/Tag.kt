package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID


@Serializable
data class Tag(
    val id: String,
    val tagName: String
)

class TagDTO (
    val id: UUID,
    val tagName: String
)

object TagModel: Table("tag") {
    private val id: Column<UUID> = TagModel.uuid("id")
    private val tagName: Column<String> = TagModel.varchar("tag_name", 255)

    fun create(tagDTO: TagDTO) {
        println("TagModel create(tagDTO: TagDTO) START")
        transaction {
            TagModel.insert {
                it[id] = tagDTO.id
                it[tagName] = tagDTO.tagName
            }
        }
    }

    fun fetch(id: UUID): TagDTO? {
        println("TagModel fetch(id: UUID): TagDTO? START")
        return try {
            transaction {
                val tagModel = TagModel.select{ TagModel.id.eq(id)}.single()
                TagDTO (
                    id = tagModel[TagModel.id],
                    tagName = tagModel[tagName]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchWithName(name: String): TagDTO? {
        return try {
            transaction {
                val tagModel = TagModel.select(tagName.eq(name)).single()
                TagDTO (
                    id = tagModel[TagModel.id],
                    tagName = tagModel[tagName]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun delete(id: UUID) {
        println("TagModel delete(id: UUID) START")
        transaction {
            TagModel.deleteWhere { TagModel.id eq id }
        }
    }

}

//TODO: TagsModel

@Serializable
data class TagsSetReceiveRemote (
    val cardID: String,
    val tagID: String
)

@Serializable
data class TagsCreateReceiveRemote (
    val tagName: String,
    val cardID: String
)

@Serializable
data class TagRemoteResponse (
    val tagID: String,
    val tagName: String,
    val tagsID: String,
    val cardID: String,
)

class TagsDTO (
    val id: UUID,
    val cardID: UUID,
    val tagID: UUID
)

object TagsModel: Table("tags") {
    private val id: Column<UUID> = TagsModel.uuid("id")
    private val cardID: Column<UUID> = TagsModel.uuid("card_id")
    private val tagID: Column<UUID> = TagsModel.uuid("tag_id")

    fun create(tagsDTO: TagsDTO) {
        println("TagModel create(id: UUID, cardID: UUID, tagID: UUID) START")
        transaction {
            TagsModel.insert {
                it[id] = tagsDTO.id
                it[cardID] = tagsDTO.cardID
                it[tagID] = tagsDTO.tagID
            }
        }
    }

    fun fetchAllTags(cardID: UUID): List<TagsDTO>? {
        println("TagModel fetchAllTags(cardID: UUID) START")
        return try {
            transaction {
                val tagsModel = TagsModel.select { TagsModel.cardID.eq(cardID)}
                tagsModel.map {
                    TagsDTO(
                        id = it[TagsModel.id],
                        cardID = it[TagsModel.cardID],
                        tagID = it[tagID]
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun delete(tagsDTO: TagsDTO) {
        println("TagModel delete(tagsDTO: NewTagsDTO) START")
        transaction {
            TagsModel.deleteWhere { id.eq(tagsDTO.id) }
        }
    }

}






































































