package com.example.feauteres.controllers

import com.example.feauteres.model.*
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

    fun setTagsForCard(tags: List<TagsDTO>) {
        println("TagController setTagsForCard(tags: List<NewTagsDTO>) START")
        tags.map {
            TagsModel.create(it)
        }
    }

    fun createTagsForCard(tags: List<TagsCreateReceiveRemote>): List<TagsSetReceiveRemote> {
        val tagsReturn: MutableList<TagsSetReceiveRemote> = arrayListOf()
        setTagsForCard( tags.map {
            val tag = createTag(it.tagName)
            val tagsID = UUID.randomUUID()
            tagsReturn.add(TagsSetReceiveRemote(cardID = it.cardID, tagID = tag.id))
            TagsDTO(
                id = tagsID,
                cardID = UUID.fromString(it.cardID),
                tagID = UUID.fromString(tag.id)
            ) }
        )
        return tagsReturn
    }

    fun fetchCardTags(cardID: UUID): List<TagRemoteResponse>? {
        val tags = TagsModel.fetchAllTags(cardID) ?: return null
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

    fun removeTagsForCard(tags: List<TagsDTO>) {
        tags.map {
            TagsModel.delete(it)
        }
    }

    fun getAllTagsForCard(cardID: UUID): List<TagsDTO>? {
        return TagsModel.fetchAllTags(cardID)
    }


}








































