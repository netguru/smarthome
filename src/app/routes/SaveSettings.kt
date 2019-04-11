package app.routes

import app.Server
import app.SettingsReq
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.json.toJson
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.put

fun Route.saveSettings(config: Config) {

    put("/save_settings") {
        val settings = call.receive<SettingsReq>()

        config[Server.dbUrl] = settings.dbUrl
        config[Server.dbUser] = settings.dbUser
        config[Server.dbPass] = settings.dbPass
        config[Server.mqttUrl] = settings.mqttUrl
        config[Server.mqttUser] = settings.mqttUser
        config[Server.mqttPass] = settings.mqttPass

        config.toJson.toFile("./config.json")
    }
}