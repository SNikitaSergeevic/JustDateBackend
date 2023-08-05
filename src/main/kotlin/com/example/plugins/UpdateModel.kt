package com.example.plugins

import com.example.feauteres.controllers.*
import com.example.feauteres.controllers.OwnerRemoteController
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Application.configureUpdateModel() {
    val imagesController = ImagesController()

    routing {
        authenticate("auth-jwt") {
            post(Endpoint.UpdateOwner.str) {
                try {
                    val updateController = OwnerRemoteController(call)
                    updateController.updateOwner()
                    call.respond(HttpStatusCode.OK, "Owner updated")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict)
                }
            }

            post(Endpoint.DeleteOwner.str) {
                val ownerController = OwnerRemoteController(call)
                ownerController.deleteOwner()
            }

            post(Endpoint.SetImage.str) {
                val multipart = call.receiveMultipart()
                imagesController.setImage(multipart = multipart)
            }


        }
    }

}







// TODO: bad work snippet
//val multipart = call.receiveMultipart()
//                var fileName: String? = null
//                var text: String? = null
//                try {
//                    multipart.forEachPart {partData ->
//                        when(partData) {
//                            is PartData.FormItem -> {
//                                if (partData.name == "text") {
//                                    text = partData.value
//                                }
//                            }
//                            is PartData.FileItem ->{
//                                fileName = partData.save("static.users.$text")
//                            }
//                            is PartData.BinaryItem -> Unit
//                            is PartData.BinaryChannelItem -> TODO()
//                        }
//                    }
//                    val imageUrl = "0.0.0.0:8443/src/main/resources/static/users/$text/$fileName"
//                    call.respond(HttpStatusCode.OK, imageUrl)
//                } catch (e: Exception) {
//                    File("static.users/$fileName").delete()
//                    call.respond(HttpStatusCode.InternalServerError, "Error")
//                }




//fun PartData.FileItem.save(path: String): String {
//    val fileBytes = streamProvider().readBytes()
//    val fileExtension = originalFileName?.takeLastWhile { it != '.'}
//    val fileName = UUID.randomUUID().toString() + "." + fileExtension
//    val folder = File("/static.users.$path")
//
//
//    folder.mkdir()
//
//
//    println("Path = $path $fileName")
//    File("$path$fileName").writeBytes(fileBytes)
//    return fileName
//}
