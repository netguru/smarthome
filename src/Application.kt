package com.netguru


import com.netguru.db.Database
import com.netguru.di.DbModule
import com.netguru.di.MainModule
import com.sun.corba.se.spi.orbutil.threadpool.Work
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.installKoin
import java.text.DateFormat
import org.eclipse.paho.client.mqttv3.MqttAsyncClient as PahoAsync


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    installKoin(listOf(DbModule, MainModule))

    install(CORS) {
        anyHost()
        method(HttpMethod.Get)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.AccessControlAllowMethods)
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.SHORT)
        }
    }

    val db by inject<Database>()
    val worker by inject<MqttWorker>()
    val subscribeChannel by inject<Channel<WorkerCmd>>("workerChannel")

    GlobalScope.launch {
        worker.doWork()
    }

    routing {
        trace { application.log.trace(it.buildText()) }

        get("/get_sensors_all") {
            //returns all sensors
            val sensors = db.getAllSensors()
            call.respond(sensors)
        }

        put("/add_sensor") {
            val addSensorReq = call.receive<AddSensorReq>()
            //TODO: add error handling when could not receive object
            val sensor = db.modifySensor(null, addSensorReq)
            subscribeChannel.offer(WorkerCmd.Subscribe(sensor))
            call.respond(HttpStatusCode.Created)
        }

        delete("/remove_sensor/{id}") {

            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                db.removeSensor(id)
                subscribeChannel.offer(WorkerCmd.Unsubscribe(id))
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        patch("/modify_sensor/{id}") {
            val id = call.parameters["id"]?.toInt()
            val sensorData = call.receive<AddSensorReq>()
            if (id != null) {
                subscribeChannel.offer(WorkerCmd.Unsubscribe(id))
                val sensor = db.modifySensor(id, sensorData)
                subscribeChannel.offer(WorkerCmd.Subscribe(sensor))
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }


        get("/get_events_for_transform/{id}/{limit}") {
            val id = call.parameters["id"]?.toInt()
            val limit = call.parameters["limit"]?.toInt()
            call.respond(db.getEventsForTransform(id ?: 0, limit ?: 10))
        }

        put("/add_event"){
            val event = call.receive<EventReq>()
            subscribeChannel.offer(WorkerCmd.PostEvent(event))
            call.respond(HttpStatusCode.OK)
        }

    }
}



