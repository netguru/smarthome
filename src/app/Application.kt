package app


import app.routes.*
import db.Database
import com.uchuhimo.konf.Config
import di.ConfigModule
import di.DbModule
import di.MainModule
import io.ktor.application.*
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.generateNonce
import io.ktor.websocket.WebSockets
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mqtt.MqttWorker
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.installKoin
import java.text.DateFormat
import java.time.Duration
import org.eclipse.paho.client.mqttv3.MqttAsyncClient as PahoAsync


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    installKoin(listOf(ConfigModule, DbModule, MainModule))

    install(CORS) {
        anyHost()
        method(HttpMethod.Get)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.AccessControlAllowMethods)
    }

    install(AutoHeadResponse)

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.SHORT)
        }
    }

    install(WebSockets) {
        pingPeriod = Duration.ofMinutes(1)
    }

    // This enables the use of sessions to keep information between requests/refreshes of the browser.
    install(Sessions) {
        cookie<Session>("SESSION")
    }

    // This adds an interceptor that will create a specific session in each request if no session is available already.
    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<Session>() == null) {
            call.sessions.set(Session(generateNonce()))
        }
    }

    val db by inject<Database>()
    val worker by inject<MqttWorker>()
    val config by inject<Config>()
    val websocketServer by inject<WebsocketServer>()

    db.createTables()

    GlobalScope.launch {
        worker.connectAndRun()
    }

    routing {
        trace { application.log.trace(it.buildText()) }

        getAllSensors(db)
        addSensor(db, worker)
        removeSensor(db, worker)
        getEventsForTransform(db)
        addEvent(worker)
        modifySensor(db,worker)
        saveSettings(config)
        getSettings(config)
        websocketServerSetup(websocketServer)

        static("/") {
            resources("sap")
            defaultResource("sap/index.html")
        }
    }
}

data class Session(val id: String)
