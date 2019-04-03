package app.routes

import app.EventReq
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.put
import mqtt.MqttWorker

fun Route.addEvent(worker: MqttWorker){
    put("/add_event") {
        val event = call.receive<EventReq>()
        worker.postEvent(event)
        call.respond(io.ktor.http.HttpStatusCode.OK)

    }
}