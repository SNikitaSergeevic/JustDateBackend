package com.example.feauteres.model.news

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
                val tagModel = TagModel.select{TagModel.id.eq(id)}.single()
                TagDTO (
                    id = tagModel[TagModel.id],
                    tagName = tagModel[TagModel.tagName]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchWithName(name: String): TagDTO? {
        return try {
            transaction {
                val tagModel = TagModel.select(TagModel.tagName.eq(name)).single()
                TagDTO (
                    id = tagModel[TagModel.id],
                    tagName = tagModel[TagModel.tagName]
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
data class NewTags (
    val id: String,
    val cardID: String,
    val tagID: String
)

@Serializable
data class NewTagsReceiveRemote (
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

class NewTagsDTO (
    val id: UUID,
    val cardID: UUID,
    val tagID: UUID
)

object NewTagsModel: Table("tags") {
    private val id: Column<UUID> = NewTagsModel.uuid("id")
    private val cardID: Column<UUID> = NewTagsModel.uuid("card_id")
    private val tagID: Column<UUID> = NewTagsModel.uuid("tag_id")

    fun create(tagsDTO: NewTagsDTO) {
        println("TagModel create(id: UUID, cardID: UUID, tagID: UUID) START")
        transaction {
            NewTagsModel.insert {
                it[id] = tagsDTO.id
                it[cardID] = tagsDTO.cardID
                it[tagID] = tagsDTO.tagID
            }
        }
    }

    fun fetchAllTags(cardID: UUID): List<NewTagsDTO>? {
        println("TagModel fetchAllTags(cardID: UUID) START")
        return try {
            transaction {
                val tagsModel = NewTagsModel.select {NewTagsModel.cardID.eq(cardID)}
                tagsModel.map {
                    NewTagsDTO(
                        id = it[NewTagsModel.id],
                        cardID = it[NewTagsModel.cardID],
                        tagID = it[NewTagsModel.tagID]
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun delete(tagsDTO: NewTagsDTO) {
        println("TagModel delete(tagsDTO: NewTagsDTO) START")
        transaction {
            NewTagsModel.deleteWhere { NewTagsModel.id.eq(tagsDTO.id) }
        }
    }

}






































































