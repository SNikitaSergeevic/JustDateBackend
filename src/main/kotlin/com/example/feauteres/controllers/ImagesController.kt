package com.example.feauteres.controllers

import com.example.feauteres.model.ImagesDTO
import com.example.feauteres.model.ImagesModel
import com.example.feauteres.model.ImagesResponse
import com.example.feauteres.model.UserspublicDTO
import com.example.feauteres.model.UserspublicModel
import io.ktor.http.content.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
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

    fun getImage(userspublicid: String, imageid: String): File? {
        val fileName = "/home/osmilijey/usr/projects/JustDateBackend/src/main/resources/static/users/$userspublicid/$imageid" + ".jpg"
        val file = File(fileName)
        println(fileName)
        return file
    }

    fun getIdAllImagesOfUser(userpublicid: String): List<ImagesResponse>? {
        var imagesDTO = ImagesModel.fetchWithUserspublicid(UUID.fromString(userpublicid))
        var imagesIds: List<ImagesResponse>? = emptyList()
        if (imagesDTO != null) {
            imagesIds = imagesDTO.map {ImagesResponse(id = it.id.toString(), path = it.path, userid = it.userid.toString(), filename = it.filename)}
        }

        return imagesIds
    }

    fun getImages(id: String): MutableList<File>? { // OLD: get all images with userpublicid
//        val image = ImagesModel.fetch(UUID.fromString(id))

//        return if (image != null) {
//            val fileName = image.filename
//            val file = File("src/main/resources/static/users/${image.userid}/$fileName")
//            return if (file.exists()) {
//                file
//            } else {
//                println("File not exist")
//                null
//            }
//        } else {
//            println("User this id not exist")
//            null
//        }
//        return if (image != null) {
            val projectDirAbsolutePath = Paths.get("").toAbsolutePath().toString()
            val imagesList: MutableList<File> = mutableListOf<File>()
            val resPath = Paths.get("/home/osmilijey/usr/projects/JustDateBackend/src/main/resources/static/users/$id/")
            val paths = Files.walk(resPath)
//                .filter {item -> Files.isRegularFile(item)}
//                .filter {item -> item.toString().endsWith(".txt")}
                .forEach {item ->
                    val file = File("/home/osmilijey/usr/projects/JustDateBackend/src/main/resources/static/users/$id/${item.fileName}")

                    if (file.exists()) {
                        imagesList.add(file)
                    } else {
                        println("AAAAAAAAAAAA else  ${item.fileName}")
                    }
                }
            println("AAAAAAAAAAAB $imagesList")

            return imagesList
//        } else {
//            println("AAAAAAAAAAAA null")
//            null
//        }


    }

}

