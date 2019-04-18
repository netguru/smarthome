package app.routes

import app.Server
import app.SettingsReq
import com.uchuhimo.konf.Config
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.getSettings(config: Config) {
    get("/settings") {
        val response = SettingsReq(
                config[Server.dbUrl],
                config[Server.dbUser],
                config[Server.dbPass],
                config[Server.mqttUrl],
                config[Server.mqttUser],
                config[Server.mqttPass]
            )
        call.respond(response)
    }
}