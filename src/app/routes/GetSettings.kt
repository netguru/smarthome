package app.routes

import app.Server
import app.SettingsReq
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.json.toJson
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.put

fun Route.getSettings(config: Config) {

    put("/settings") {
        call.respond(config)
    }
}