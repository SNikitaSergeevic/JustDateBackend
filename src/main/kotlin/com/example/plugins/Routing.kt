package com.example.plugins


import com.example.plugins.news.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun Application.configureRouting() {

//    configureRegistration() // V
//    configureLogin() // V
//    configureAuthorisation() // V
//    configureUpdateModel() //V
//    configureMatching() //V

    //TODO: News
    ownerConfigure()
    imageConfigure()
    matchConfigure()
    tagConfigure()



    routing {

        get(Endpoint.Check.str) {
            call.respondText("This Worked")
        }
        authenticate("auth-jwt") {
            get(Endpoint.AuthCheck.str) {
                val principal = call.principal<JWTPrincipal>()
                val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())
                val id = principal?.get("oid")
                call.respondText("Token for $id expires at $expiresAt.")
            }
//            get(Endpoint.GetImages.str) {
//                val imageid = call.parameters["imageId"]!!
//                val file = ImagesController().getImages(imageid)
//
//                if (file != null) {
//                    var images = byteArrayOf()
//                    val im = file.forEach{item ->
//                        images += item.readBytes()
//
//                    }
//                    println("BBB ${images.indices}")
//                    println(images.toString())
//                    call.respondBytes(images)
//                } else {
//                    call.respond(HttpStatusCode.NotFound)
//                }
//            }
//
//            get(Endpoint.GetImage.str) {
//                val userspublicid = call.parameters["userspublicid"]!!
//                val imageid = call.parameters["imageid"]!!
//                var file = ImagesController().getImage(userspublicid, imageid)
//                call.respondBytes(file!!.readBytes()!!)
//            }
//
//            get(Endpoint.GetImagesIdWithUserspublicid.str) {
//                val userspublicid = call.parameters["userpublicid"]!!
//                val imagesIds = ImagesController().getIdAllImagesOfUser(userspublicid)
//
//                if (imagesIds != null) {
//                    call.respond(imagesIds)
//                } else {
//                    call.respond(HttpStatusCode.NotFound)
//                }
//
//
//            }
//
//            post(Endpoint.FetchOwner.str) {
//                val ownerController = OwnerRemoteController(call)
//                val owner = ownerController.fetchOwner()
//
//                if (owner != null) {
//                    call.respond(owner)
//                } else {
//                    call.respond(HttpStatusCode.NotFound)
//                }
//            }
        }
    }
}

enum class Endpoint(val str: String) {
    Check("/check"),
    //todo: owner
    Registration("/registration"),
    Authorisation("/authorisation"),
    Login("/login"),
    AuthCheck("/authCheck"),
    DeleteOwner("/auth/deleteOwner"),
    UpdateOwner("/auth/updateOwner"),
    FetchOwner("/auth/fetchOwner/{ownerID}"),
    //todo: image
    SetImage("/auth/setImage"),
    GetImages("/auth/getImage/{imageID}"),
    GetImage("/auth/getImage/{cardID}/{imageID}"),
    GetImagesIdWithCardid("/auth/getImagesId/{cardID}"),
    //todo: cards and tags
    FetchCardsOnSex("/auth/fetchCardsOnSex"),
    FetchCardsTags("/auth/fetchCardsTags/{cardID}"),
    SetNewCardTags("/auth/setNewCardTags"),
    SetExistCardTags("/auth/setExistCardTags")
}
