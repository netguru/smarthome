package app.routes

import com.netguru.db.Database
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.getEventsForTransform(db: Database) {
    get("/get_events_for_transform/{id}/{limit}") {
        val id = call.parameters["id"]?.toInt()
        val limit = call.parameters["limit"]?.toInt()
        call.respond(db.getEventsForTransform(id ?: 0, limit ?: 10))
    }
}