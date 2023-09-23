package com.example.plugins


import com.example.feauteres.controllers.ImagesController
import com.example.feauteres.controllers.OwnerRemoteController
import com.example.feauteres.controllers.UserpublicController
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun Application.configureRouting() {

    configureRegistration()
    configureLogin()
    configureAuthorisation()
    configureUpdateModel()
    configureMatching()



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
            get(Endpoint.GetImage.str) {
                val imageid = call.parameters["imageId"]!!
                val file = ImagesController().getImage(imageid)

                if (file != null) {
                    var images = byteArrayOf()
                    val im = file.forEach{item ->
                        images += item.readBytes()

                    }
                    println("BBB ${images.indices}")
                    println(images.toString())
                    call.respondBytes(images)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            post(Endpoint.FetchOwner.str) {
                val ownerController = OwnerRemoteController(call)
                val owner = ownerController.fetchOwner()

                if (owner != null) {
                    call.respond(owner)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}

enum class Endpoint(val str: String) {
    Check("/check"),
    Registration("/registration"),
    Authorisation("/authorisation"),
    Login("/login"),
    AuthCheck("/authCheck"),
    DeleteOwner("/auth/deleteOwner"),
    UpdateOwner("/auth/updateOwner"),
    SetImage("/auth/setImage"),
    GetImage("/auth/getImage/{imageId}"),
    FetchOwner("/auth/fetchOwner"),
    FetchUserpublicOnSex("/auth/fetchUserpublicOnSex")
}
