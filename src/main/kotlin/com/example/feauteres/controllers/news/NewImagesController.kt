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



class NewImagesController() {

    suspend fun setImage(multipart: MultiPartData) {
        var text = ""
        var fileName = ""
        var card: CardDTO? = null

        multipart.forEachPart { part ->
            when(part) {
                is PartData.FormItem -> {
                    text = part.value
                    card = CardModel.fetch(text)
                }

                is PartData.FileItem -> {
                    if (card != null) {
                        val fileExtension = part.originalFileName?.takeLastWhile { it != '.' }
                        fileName = UUID.randomUUID().toString()


                        val folder = File("src/main/resources/static/users/$text")
                        folder.mkdir()

                        val file = File("src/main/resources/static/users/$text/${fileName + "." + fileExtension}")

                        part.streamProvider().use { its ->
                            file.outputStream().buffered().use {
                                its.copyTo(it)
                            }
                        }

                        val imageDTO = ImageDTO(
                            id = UUID.fromString(fileName),
                            path = "src/main/resources/static/users/$text",
                            cardID = card!!.id,
                            fileName = "${fileName + "." + fileExtension}",
                            createdAt = LocalDate.now()
                        )
                        ImageModel.create(imageDTO)


                    } else {
                        println("Card this id NOT FOUND")
                    }
                }
                is PartData.BinaryItem -> UInt
                is PartData.BinaryChannelItem -> TODO()
            }
            part.dispose()
        }
    }

    fun getImage(cardID: String, imageID: String): File? {
        val fileName = "/home/osmilijey/usr/projects/JustDateBackend/src/main/resources/static/users/$cardID/$imageID" + ".jpg"
        val file = File(fileName)
        return file
    }

    fun getAllIdImagesForCard(cardID: String): List<ImageResponse>? {
        var imageDTO = ImageModel.fetchAllForCard(UUID.fromString(cardID))
        var imageIDs: List<ImageResponse>? = emptyList()
        if (imageDTO != null) {
            imageIDs = imageDTO.map {ImageResponse(
                id = it.id.toString(),
                path = it.path,
                cardID = it.cardID.toString(),
                fileName = it.fileName,
                createdAt = it.createdAt.toString()
            )}
        }
        return imageIDs
    }

    fun getImages(id: String): MutableList<File>? {
        val imagesList: MutableList<File> = mutableListOf<File>()
        val resPath = Paths.get("/home/osmilijey/usr/projects/JustDateBackend/src/main/resources/static/users/$id/")
        Files.walk(resPath)
            .forEach {item ->
                val file = File("/home/osmilijey/usr/projects/JustDateBackend/src/main/resources/static/users/$id/${item.fileName}")
                if (file.exists()) {
                    imagesList.add(file)
                }
            }
        return imagesList
    }




    



}


































