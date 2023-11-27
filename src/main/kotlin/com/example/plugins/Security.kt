package com.example.plugins

import com.example.feauteres.model.ChatReceiveRemote
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
        cookie<ChatReceiveRemote>("SESSION")
    }

    intercept(Plugins) {
        if(call.sessions.get<ChatReceiveRemote>() == null) {
//            val username = call.parameters["username"] ?: "Guest"
//            call.sessions.set(ChatSession(username, generateNonce()))

            val chat = call.receive<ChatReceiveRemote>()
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
