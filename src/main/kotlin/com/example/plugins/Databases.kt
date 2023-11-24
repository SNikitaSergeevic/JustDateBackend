package com.example.plugins

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.*
import java.sql.*
import org.jetbrains.exposed.sql.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureDatabases() {
    
//    val database = Database.connect(
//            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
//            user = "root",
//            driver = "org.h2.Driver",
//            password = ""
//        )

    val url1 = "jdbc:postgresql://localhost:5432/justdatedb.justdate_schema"
    val url2 = "jdbc:postgresql://192.168.0.100:5432/justdatedb_1"
    val url3 = "jdbc:postgresql://localhost:5432/justdatedb_1"

    Database.connect(
        url3,
        driver = "org.postgresql.Driver",
        user = "osmilijey",
        password = "16710985") // local


//    Database.connect("jdbc:postgresql://192.168.0.100:5432/justdatedb_1",
//        driver = "org.postgresql.Driver",
//        user = "osmilijey",
//        password = "16710985") // net

//    val dbConnection: Connection = connectToPostgres(embedded = false)

}
/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.user -- Username for database connection
 * - postgres.password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be able to edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 *
 * @param embedded -- if [true] defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
//fun Application.connectToPostgres(embedded: Boolean): Connection {
//    Class.forName("org.postgresql.Driver")
//    if (embedded) {
//        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
//    } else {
//        //todo: url1 - old db; url2 - new db for net connect; url3 - new db for local connect
//        val url1 = environment.config.property("jdbc:postgresql://localhost:5432/justdatedb.justdate_schema").getString()
//        val url2 = environment.config.property("jdbc:postgresql://192.168.0.100:5432/justdatedb_1").getString()
//        val url3 = environment.config.property("jdbc:postgresql://localhost:5432/justdatedb_1").getString()
//
//
//        val user = environment.config.property("osmilijey").getString()
//        val password = environment.config.property("16710985").getString()
//
//        return DriverManager.getConnection(url2, user, password)
//    }
//}

