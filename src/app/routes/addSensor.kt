package app.routes

import app.AddSensorReq
import mqtt.MqttWorker
import db.Database
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.put

fun Route.addSensor(db: Database, worker: MqttWorker) {
    put("/add_sensor") {
        val addSensorReq = call.receive<AddSensorReq>()
        val sensor = db.modifySensor(null, addSensorReq)
        worker.subscribe(sensor)
        call.respond(io.ktor.http.HttpStatusCode.Created)

    }
}