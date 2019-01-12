package com.netguru

import com.github.jasync.sql.db.Configuration
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.general.ArrayRowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.pool.PoolConfiguration
import com.github.jasync.sql.db.postgresql.pool.PostgreSQLConnectionFactory
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
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
    connection.connect().get()

    val db = Database(connection)

    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }

    //TODO: refactor this into separate file and function
    GlobalScope.launch {
        val mqttClient = MqttClient(PahoAsync("tcp://192.168.0.21:1883", "ktor-server"))
        mqttClient.connect("pi", "spitulis")
        log.debug("mqtt connected")

        for (message in mqttClient.subscribe(MqttClient.ALL_TOPICS)) {
            log.debug("Received $message")
        }
    }

    routing {
        trace { application.log.trace(it.buildText()) }
        get("/") {
            //status page

        }

        get("/admin") {
            //admin page (add and remove sensors)
            val sensors = db.getAllSensors()
            call.respondHtml {
                body {
                    ul {
                        sensors.forEach { sensor ->
                            li { +"${sensor.name} - ${sensor.topic} - ${sensor.transform}" }
                        }
                    }
                }
            }
        }

        get("/get_all"){
            //returns all sensors
        }

        get("/get_name"){
            //return sensor for name
        }

        get("/get_all_for_topic"){
            //return all sensors for topic
        }

        put("/add_sensor"){
            val sensor = call.receive<AddSensorReq>()
            //TODO: add error handling when could not receive object
            db.addSensor(sensor)
            call.respond(HttpStatusCode.Created)
        }

        delete("/remove_sensor"){
            //remove by id
        }

        delete("/remove_by_topic"){
            //remove all sensors that match topic
        }

    }
}




