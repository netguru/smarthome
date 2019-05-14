package app.routes

import mqtt.MqttWorker
import db.Database
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete

fun Route.removeSensor(db: Database, worker: MqttWorker) {
    delete("/remove_sensor/{id}") {

        val id = call.parameters["id"]?.toInt()
        if (id != null) {
            db.removeSensor(id)
            worker.unsubscribe(id)
            call.respond(HttpStatusCode.OK)

        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}