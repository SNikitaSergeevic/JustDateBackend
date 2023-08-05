package com.example.feauteres.controllers

import com.example.feauteres.model.ImagesDTO
import com.example.feauteres.model.ImagesModel
import com.example.feauteres.model.UserspublicDTO
import com.example.feauteres.model.UserspublicModel
import io.ktor.http.content.*
import java.io.File
import java.util.*


class ImagesController() {
   suspend fun setImage(multipart: MultiPartData) {
        var text = ""
        var fileName = ""
       var userpublic: UserspublicDTO? = null

        multipart.forEachPart {  part ->

            when(part) {

                is PartData.FormItem -> {
                    text = part.value
                    userpublic = UserspublicModel.fetch(text)
                }

                is PartData.FileItem -> {
                    if (userpublic != null) {
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

                        val imagesDTO = ImagesDTO(id = UUID.fromString(fileName),
                                                path = "src/main/resources/static/users/$text",
                                                userid = userpublic!!.id,
                                                filename = "${fileName + "." + fileExtension}")
                        ImagesModel.insert(imagesDTO)
                        
                    } else {
                        println("Userpublic this id NOT FOUND")
                    }

                }

                is PartData.BinaryItem -> Unit

                is PartData.BinaryChannelItem -> TODO()
            }
            part.dispose()
        }
    }

    suspend fun getImage(id: String): File? {
        val image = ImagesModel.fetch(UUID.fromString(id))

        return if (image != null) {
            val fileName = image.filename
            val file = File("src/main/resources/static/users/${image.userid}/$fileName")
            return if (file.exists()) {
                file
            } else {
                println("File not exist")
                null
            }
        } else {
            println("User this id not exist")
            null
        }
    }

}

