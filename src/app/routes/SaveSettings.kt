package app.routes

import app.SettingsReq
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.put

fun Route.saveSettings() {

    put("/save_settings") {
        val settings = call.receive<SettingsReq>()

        // saveConfiguration
//            db.connect(settings.dbUrl, settings.dbUser, settings.dbPass)
//            worker.connect(settings.mqttUrl, settings.mqttUser, settings.mqttPass)
    }
}