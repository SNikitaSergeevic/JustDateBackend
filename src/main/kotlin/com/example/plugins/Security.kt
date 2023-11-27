package com.example.plugins

import com.example.feauteres.model.ChatReceiveRemote
import com.example.feauteres.model.SessionData
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Application.configureSecurity() {
//    data class MySession(val count: Int = 0)
    install(Sessions) {
//        cookie<MySession>("MY_SESSION") {
//            cookie.extensions["SameSite"] = "lax"
//        }
        cookie<SessionData>("SESSION")

    }

    intercept(Plugins) {
        val session = call.sessions.get<SessionData>()
        if(session == null) {
//            val username = call.parameters["username"] ?: "Guest"
//            call.sessions.set(ChatSession(username, generateNonce()))
            println("\n ==== SessionNotExist === \n")
            val ownerID = call.parameters["ownerID"].toString()
            call.sessions.set(SessionData(ownerID))

        } else {
            println("\n ==== SessionExist === \n")
            val chat = call.receive<SessionData>()
            call.sessions.set(chat)
        }
    }
//    routing {
//        get("/session/increment") {
//                val session = call.sessions.get<MySession>() ?: MySession()
//                call.sessions.set(session.copy(count = session.count + 1))
//                call.respondText("Counter is ${session.count}. Refresh to increment.")
//            }
//    }
}
