package app.routes

import db.Database
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.getAllSensors(db: Database) {
    get("/get_sensors_all") {
        //returns all sensors
        val sensors = db.getAllSensors()
        call.respond(sensors)
    }
}