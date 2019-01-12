package com.netguru

import com.github.jasync.sql.db.Configuration
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.general.ArrayRowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.pool.PoolConfiguration
import com.github.jasync.sql.db.postgresql.pool.PostgreSQLConnectionFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.html.*
import java.util.concurrent.TimeUnit

import org.eclipse.paho.client.mqttv3.MqttAsyncClient as PahoAsync

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val connection: Connection = ConnectionPool(
        PostgreSQLConnectionFactory(
            Configuration(
                username = "pi",
                password = "spitulis",
                host = "192.168.0.21",
                port = 5432,
                database = "pi"
            )
        ), PoolConfiguration(
            20,
            TimeUnit.MINUTES.toMillis(15),  // maxIdle
            10_000,                         // maxQueueSize
            TimeUnit.SECONDS.toMillis(30)   // validationInterval
        )
    )

    GlobalScope.launch {
        val mqttClient = MqttClient(PahoAsync("tcp://192.168.0.21:1883", "ktor-server"))
        mqttClient.connect("pi", "spitulis")
        log.debug("mqtt connected")

        connection.connect().get()
        val queryResult = connection.sendPreparedStatementAwait("select * from test")
        for (rowData in queryResult.rows) {
            val id = rowData.getInt("id")
            val test = rowData.getString("test")
            log.debug("BOCHEN = $id, $test")
        }

        for (message in mqttClient.subscribe(MqttClient.ALL_TOPICS)) {
            log.debug("Received $message")
        }
    }

    routing {
        get("/") {
            //status page
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/admin") {
            //admin page (add and remove sensors)
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        put("/add_sensor"){

        }

        delete("/remove_sensor"){

        }

    }
}


suspend fun Connection.sendPreparedStatementAwait(query: String, values: List<Any> = emptyList()): QueryResult =
    this.sendPreparedStatement(query, values).await()

