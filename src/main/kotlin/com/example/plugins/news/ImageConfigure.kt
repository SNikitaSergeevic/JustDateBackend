package com.example.plugins.news

import com.example.feauteres.controllers.ImagesController
import com.example.plugins.Endpoint
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*


fun Application.imageConfigure() {

    routing {
        authenticate("auth-jwt") {
            get(Endpoint.GetImages.str) {
                val imageID = call.parameters["imageID"]!!
                val file = ImagesController().getImages(imageID)

                if (file != null) {
                    var images = byteArrayOf()
                    file.forEach { item ->
                        images += item.readBytes()
                    }
                    call.respondBytes(images)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            get(Endpoint.GetImage.str) {
                val cardID = call.parameters["cardID"]!!
                val imageID = call.parameters["imageID"]!!
                val file = ImagesController().getImage(cardID, imageID)
                call.respondBytes(file!!.readBytes()!!)
            }

            get(Endpoint.GetImagesIdWithCardid.str) {
                val cardID = call.parameters["cardID"]!!
                val imageIDs = ImagesController().getAllIdImagesForCard(cardID)

                if (imageIDs != null) {
                    call.respond(imageIDs)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post(Endpoint.SetImage.str) {
                val multipart = call.receiveMultipart()
                val imageController = ImagesController()
                imageController.setImage(multipart)
            }

        }
    }

}