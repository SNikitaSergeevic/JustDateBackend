package com.example

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ModuleTest {

    @Test
    fun testGet() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetJsonKotlinxserialization() = testApplication {
        application {
            module()
        }
        client.get("/json/kotlinx-serialization").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetSessionIncrement() = testApplication {
        application {
            module()
        }
        client.get("/session/increment").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testWebsocketWs() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(WebSockets)
        }
        client.webSocket("/ws") {
            TODO("Please write your test here")
        }
    }
}