package com.example.feauteres.controllers.news

import com.example.feauteres.model.news.*
import io.ktor.http.content.*
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

    fun createTagsForCard(tags: List<NewTagsCreateReceiveRemote>): List<NewTagsSetReceiveRemote> {
        val tagsReturn: MutableList<NewTagsSetReceiveRemote> = arrayListOf()
        setTagsForCard( tags.map {
            val tag = createTag(it.tagName)
            val tagsID = UUID.randomUUID()
            tagsReturn.add(NewTagsSetReceiveRemote(cardID = it.cardID, tagID = tag.id))
            NewTagsDTO(
                id = tagsID,
                cardID = UUID.fromString(it.cardID),
                tagID = UUID.fromString(tag.id)
            ) }
        )
        return tagsReturn
    }

    fun fetchCardTags(cardID: UUID): List<TagRemoteResponse>? {
        val tags = NewTagsModel.fetchAllTags(cardID) ?: return null
        val allTagCard = tags.map {
            val tag = TagModel.fetch(it.tagID) ?: return null
             TagRemoteResponse(
                tagID = tag.id.toString(),
                tagName = tag.tagName,
                tagsID = it.id.toString(),
                cardID = it.cardID.toString()
            )
        }
        return allTagCard
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








































