package com.netguru

import com.github.jasync.sql.db.Configuration
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.pool.PoolConfiguration
import com.github.jasync.sql.db.postgresql.pool.PostgreSQLConnectionFactory
import com.jayway.jsonpath.JsonPath
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.concurrent.TimeUnit

import org.eclipse.paho.client.mqttv3.MqttAsyncClient as PahoAsync

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS){
        anyHost()
        method(HttpMethod.Get)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.AccessControlAllowMethods)
    }

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
            setDateFormat(DateFormat.SHORT)
        }
    }

    val subscribeChannel = Channel<WorkerCmd>()
    val mqttClient = MqttClient(PahoAsync("tcp://192.168.0.21:1883", "ktor-server"))
    val worker = MqttWorker(mqttClient, db, subscribeChannel, log)

    GlobalScope.launch {
        worker.doWork()
    }

    routing {
        trace { application.log.trace(it.buildText()) }

        get("/get_sensors_all"){
            //returns all sensors
            call.respond (db.getAllSensors())
        }

        put("/add_sensor"){
            val addSensorReq = call.receive<AddSensorReq>()
            //TODO: add error handling when could not receive object
            val sensor = db.addSensor(addSensorReq)
            subscribeChannel.offer(WorkerCmd.Subscribe(sensor))
            call.respond(HttpStatusCode.Created)
        }

        delete("/remove_sensor/{id}"){

            val id = call.parameters["id"]?.toInt()
            if(id!= null){
                db.removeSensor(id)
                subscribeChannel.offer(WorkerCmd.Unsubscribe(id))
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        patch("/modify_sensor/{id}"){
            val id = call.parameters["id"]?.toInt()
            val sensorData = call.receive<AddSensorReq>()
            if(id != null){
                subscribeChannel.offer(WorkerCmd.Unsubscribe(id))
                val sensor = db.modifySensor(id, sensorData)
                subscribeChannel.offer(WorkerCmd.Subscribe(sensor))
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }


        get("/get_events_for_sensor/{id}/{limit}"){
            val id = call.parameters["id"]?.toInt()
            val limit = call.parameters["limit"]?.toInt()
            call.respond(db.getEvents(id?:0, limit?:10))
        }

    }
}



