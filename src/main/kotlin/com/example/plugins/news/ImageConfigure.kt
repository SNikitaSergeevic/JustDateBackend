package com.example.plugins.news

import com.example.feauteres.controllers.ImagesController
import com.example.feauteres.controllers.OwnerRemoteController
import com.example.feauteres.controllers.UserpublicController
import com.example.feauteres.controllers.news.NewImagesController
import com.example.plugins.Endpoint
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*


fun Application.imageConfigure() {

    routing {
        authenticate("auth-jwt") {
            get(Endpoint.GetImage.str) {
                val imageID = call.parameters["imageId"]!!
                val file = NewImagesController().getImages(imageID)

                if (file != null) {
                    var images = byteArrayOf()
                    file.forEach{item ->
                        images += item.readBytes()
                    }
                    call.respondBytes(images)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            get(Endpoint.GetImage.str) {
                val cardID = call.parameters["userspublicid"]!!
                val imageID = call.parameters["imageid"]!!
                val file = NewImagesController().getImage(cardID, imageID)
                call.respondBytes(file!!.readBytes()!!)
            }

            get(Endpoint.GetImagesIdWithUserspublicid.str) {
                val cardID = call.parameters["userspublicid"]!!
                val imageIDs = NewImagesController().getAllIdImagesForCard(cardID)

                if (imageIDs != null) {
                    call.respond(imageIDs)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post(Endpoint.SetImage.str) {
                val multipart = call.receiveMultipart()
                val imageController = NewImagesController()
                imageController.setImage(multipart)
            }

        }
    }

}