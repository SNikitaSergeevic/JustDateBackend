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
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.core.Koin
import org.koin.ktor.ext.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.*
import com.example.mainModule


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

/*
    1. Регистрация юзера
        - приходят мыло, пароль и др данные
        - мы это добро берем, валидируем
        - если все норм то записываем в бд
        - создаем access token, refresh token(хэш мыла) и ownerid пишем в бд
        - уже после этого возвращаем в ответе  - access token, refresh token и ownerid в JWT
    2. Логин
        - приходит пароль и почта
        - чекаем их и если все норм
        - удаляем рефреш токен
        - надо ли удалять ацесс токен?
        - выдаем ацесс токен
        - возвращаем JWT с теми же данными что и при регистрации
    3. Авторизация (при помощи рефреш токена)
        - отправляем ownerid и refreshToken
        - проверяем их наличие и совпадение в бд
        - если все норм то удаляем ревреш токен и устанавливаем новый
        - ownerid и новый refreshToken отправляем
    4. Update owner есть --- надо получше обработать данные и добавить фотки


*/
