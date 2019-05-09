package app.routes

import app.AddSensorReq
import mqtt.MqttWorker
import db.Database
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.patch

fun Route.modifySensor(db: Database, worker: MqttWorker){
    patch("/modify_sensor/{id}") {
        val id = call.parameters["id"]?.toInt()
        val sensorData = call.receive<AddSensorReq>()
        if (id != null) {
            worker.unsubscribe(id)
            val sensor = db.modifySensor(id, sensorData)
            worker.subscribe(sensor)
            call.respond(io.ktor.http.HttpStatusCode.OK)
        } else {
            call.respond(io.ktor.http.HttpStatusCode.BadRequest)
        }
    }
}