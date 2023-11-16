package com.example.feauteres.controllers.news

import com.example.feauteres.model.ImagesDTO
import com.example.feauteres.model.ImagesModel
import com.example.feauteres.model.ImagesResponse
import com.example.feauteres.model.UserspublicDTO
import com.example.feauteres.model.UserspublicModel
import com.example.feauteres.model.news.*
import io.ktor.http.content.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.util.*



class TagController() {

    private fun createTag(tagName: String): Tag {
        val tagExist = TagModel.fetchWithName(tagName)
        return if (tagExist == null) {
            val tagDTO = TagDTO(
                id = UUID.randomUUID(),
                tagName = tagName
            )
            TagModel.create(tagDTO)
            Tag(tagDTO.id.toString(), tagDTO.tagName)
        } else {
            Tag(tagExist.id.toString(), tagExist.tagName)
        }
    }

    fun setTagsForCard(tags: List<NewTagsDTO>) {
        println("TagController setTagsForCard(tags: List<NewTagsDTO>) START")
        tags.map {
            NewTagsModel.create(it)
        }
    }

    fun createTagsForCard(tags: List<NewTagsReceiveRemote>): List<NewTags>? {
        val tagsReturn: MutableList<NewTags> = arrayListOf()
        setTagsForCard( tags.map {
            val tag = createTag(it.tagName)
            val tagsID = UUID.randomUUID()
            tagsReturn.add(NewTags(id = tagsID.toString(), cardID = it.cardID, tagID = tag.id))
            NewTagsDTO(
                id = tagsID,
                cardID = UUID.fromString(it.cardID),
                tagID = UUID.fromString(tag.id)
            ) }
        )
        return tagsReturn
    }

    fun removeTagsForCard(tags: List<NewTagsDTO>) {
        tags.map {
            NewTagsModel.delete(it)
        }
    }

    fun getAllTagsForCard(cardID: UUID): List<NewTagsDTO>? {
        return NewTagsModel.fetchAllTags(cardID)
    }


}








































