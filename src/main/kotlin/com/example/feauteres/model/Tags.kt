package com.example.feauteres.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Serializable
data class Tags (val id: String, val text: String)

class TagsDTO (val id: UUID, val text: String)

object TagsModel: Table("justdate_schema.tags") {
    private val id = TagsModel.uuid("id")
    private val text = TagsModel.varchar("text", 255)

    fun insert(tagsDTO: TagsDTO) {
        println("insertTags START")
        transaction {
            TagsModel.insert {
                it[id] = tagsDTO.id
                it[text] = tagsDTO.text
            }
        }
    }

    fun fetch(id: UUID): TagsDTO? {
        println("fetchTags START")
        return try {
            transaction {
                val tagsModel = TagsModel.select {TagsModel.id.eq(id)}.single()
                TagsDTO (id = tagsModel[TagsModel.id],
                    text = tagsModel[TagsModel.text])
            }
        } catch (e: Exception) {
            null
        }
    }

    fun delete(id: UUID) {
        println("deleteTags START")
        transaction {
            TagsModel.deleteWhere {TagsModel.id eq id}
        }
    }

}

// TODO: new TagModel

object TagModel: Table("justdate_schema.tag") {
    private val id: Column<UUID> = TagModel.uuid("id")
    private val tag_name: Column<String> = TagModel.varchar("tag_name", 255)
}

object NewTagsModel: Table("justdate_schema.tags") {
    private val id: Column<UUID> = NewTagsModel.uuid("id")
    private val cardID: Column<UUID> = NewTagsModel.uuid("card_id")
    private val tadID: Column<UUID> = NewTagsModel.uuid("tag_id")
}