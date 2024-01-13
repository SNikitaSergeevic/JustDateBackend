package com.example.feauteres.controllers

import com.example.feauteres.model.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*



class ImagesController() {

    suspend fun setImage(multipart: MultiPartData) {
        var cardID = ""
        var fileName = ""
        var card: CardDTO? = null

        multipart.forEachPart { part ->
            when(part) {
                is PartData.FormItem -> {
                    cardID = part.value
                    card = CardModel.fetch(UUID.fromString(cardID))
                }

                is PartData.FileItem -> {
                    if (card != null) {
                        val fileExtension = part.originalFileName?.takeLastWhile { it != '.' }
                        fileName = UUID.randomUUID().toString()


                        val folder = File("src/main/resources/static/users/$cardID")
                        folder.mkdir()

                        val file = File("src/main/resources/static/users/$cardID/${fileName + "." + fileExtension}")

                        part.streamProvider().use { its ->
                            file.outputStream().buffered().use {
                                its.copyTo(it)
                            }
                        }

                        val imageDTO = ImageDTO(
                            id = UUID.fromString(fileName),
                            path = "src/main/resources/static/users/$cardID",
                            cardID = card!!.id,
                            fileName = "${fileName + "." + fileExtension}",
                            createdAt = java.util.Date().time
                        )
                        ImageModel.create(imageDTO)


                    } else {
                        println("Card id NOT FOUND")
                    }
                }
                is PartData.BinaryItem -> UInt
                is PartData.BinaryChannelItem -> TODO()
            }
            part.dispose()
        }
    }

    suspend fun setImageDev(call: ApplicationCall) {
        val cardID = call.parameters["cardID"]
        val validToken = false
        var remoteRToken = ""
        var fileName = ""
        var card: CardDTO? = CardModel.fetch(UUID.fromString(cardID))
        val multipart = call.receiveMultipart()

        if (card != null) {
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        remoteRToken = part.value
                    }

                    is PartData.FileItem -> {
                        if (RefreshTokenModel.tokenCheck(remoteRToken)) {
                            val fileExtension = part.originalFileName?.takeLastWhile { it != '.' }
                            fileName = UUID.randomUUID().toString()

                            val folder = File("src/main/resources/static/users/$cardID")
                            folder.mkdir()

                            val file = File("src/main/resources/static/users/$cardID/${fileName + "." + fileExtension}")
                            part .streamProvider().use { its ->
                                file.outputStream().buffered().use {
                                    its.copyTo(it)
                                }
                            }

                            val imageDTO = ImageDTO (
                                id = UUID.fromString(fileName),
                                path = "stc/main/resources/static/users/$cardID",
                                cardID = card.id,
                                fileName = "${fileName + "." + fileExtension}",
                                createdAt = java.util.Date().time
                            )
                            ImageModel.create(imageDTO)
                        } else {
                            call.respond(HttpStatusCode.Conflict, "invalid token")
                        }
                    }

                    is PartData.BinaryItem -> UInt
                    is PartData.BinaryChannelItem -> TODO()
                }
                part.dispose()
            }
        } else {
            call.respond(HttpStatusCode.NotFound, "card not found")
        }


    }

    fun getImage(cardID: String, imageID: String): File? {
        val fileName = "/home/osmilijey/usr/projects/JustDateBackend/src/main/resources/static/users/$cardID/$imageID" + ".jpg"
        val file = File(fileName)
        return file
    }

    fun getAllIdImagesForCard(cardID: String): List<ImageResponse>? {
        println("NewImagesControlller getAllIdImagesForCard(cardID: String) START")
        var imageDTO = ImageModel.fetchAllForCard(UUID.fromString(cardID))
        var imageIDs: List<ImageResponse>? = emptyList()
        if (imageDTO != null) {
            imageIDs = imageDTO.map {
                ImageResponse(
                id = it.id.toString(),
                path = it.path,
                cardID = it.cardID.toString(),
                fileName = it.fileName,
                createdAt = it.createdAt
            )
            }
        }
        println("NewImagesControlller getAllIdImagesForCard(cardID: String) imageIDs - ${imageIDs!!.count()}")
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


































