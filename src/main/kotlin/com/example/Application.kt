package com.example


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.network.tls.certificates.*
import com.example.plugins.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.websocket.*
//import org.koin.core.Koin
//import org.koin.ktor.ext.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.*
import io.ktor.server.websocket.*


//fun main() {
//
//    val keyStoreFile = File("build/keystore.jks")
//    val keyStore = buildKeyStore {
//        certificate("sampleAlias") {
//            password = "foobar"
//            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
//        }
//    }
//    keyStore.saveToFile(keyStoreFile, "123456")
//
//    val environment = applicationEngineEnvironment {
//        log = LoggerFactory.getLogger("ktor.application")
//        connector {
//            port = 8080
//        }
//        sslConnector(
//            keyStore = keyStore,
//            keyAlias = "sampleAlias",
//            keyStorePassword = {"123456".toCharArray()},
//            privateKeyPassword = {"foobar".toCharArray()}) {
//            port = 8443
//            keyStorePath = keyStoreFile
//        }
//        module(Application::module)
//    }
//
//    embeddedServer(Netty, environment).start(wait = true)
//
////    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
////        .start(wait = true)
//}


fun main(args: Array<String>): Unit = EngineMain.main(args)


fun Application.module() {




    configureSockets()
    configureRouting()
    configureSerialization()
    configureDatabases()
//    configureHTTP()  //swagger config
    configureSecurity()
}

