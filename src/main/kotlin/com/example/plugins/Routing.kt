package com.example.plugins


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.feauteres.controllers.ChatController
import com.example.plugins.news.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.websocket.*
import java.time.Duration

//import org.koin.java.KoinJavaComponent.inject
//import org.koin.ktor.ext.inject


fun Application.configureRouting() {

//    val chatController by inject<ChatController>()


    val chatController = ChatController()

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build())
            validate { credential ->
                if (credential.payload.getClaim("id").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultSchema, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    //TODO: News
//    install(Routing) {
//        ownerConfigure(
//            secret = secret,
//            issuer = issuer,
//            audience = audience,
//            myRealm = myRealm
//        )
//        cardConfigure()
//        imageConfigure()
//        matchConfigure()
//        tagConfigure()
////        chatConfigure(chatController)
//    }

    ownerConfigure(secret, issuer, audience, myRealm)
    cardConfigure()
    imageConfigure()
    matchConfigure()
    tagConfigure()
    chatConfigure(chatController)

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
    FetchPublicOwner("/auth/fetchPublicOwner/{ownerID}"),
    FetchPrivateOwner("/auth/fetchPrivateOwner}"),
    //todo: image
    SetImage("/auth/setImage"),
    GetImages("/auth/getImage/{imageID}"),
    GetImage("/auth/getImage/{cardID}/{imageID}"),
    GetImagesIdWithCardid("/auth/getImagesId/{cardID}"),
    //todo: cards and tags
    FetchCardsOnSex("/auth/fetchCardsOnSex"),
    FetchCardsTags("/auth/fetchCardsTags/{cardID}"),
    SetNewCardTags("/auth/setNewCardTags"),
    SetExistCardTags("/auth/setExistCardTags"),
    //todo: match
    FetchExistMatchSenderIDCheck("/auth/fetchExistMatchSenderIDCheck/{id}"),
    FetchExistMatchRecipientIDCheck("/auth/fetchExistMatchRecipientIDCheck/{id}"),
    UpdateMatch("/auth/updateMatch/{senCardID}/{recCardID}"),
    CreateMatch("/auth/createMatch"),
    //todo: chat
    GetChat("/auth/getChat/{ownerID}/{companionID}"),
    ChatConnect("/auth/chatConnect/{ownerID}/{companionID}")

}








