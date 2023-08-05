package com.example.plugins


import com.example.feauteres.controllers.ImagesController
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
                    call.respondFile(file)
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
    GetImage("/auth/getImage/{imageId}")
}
