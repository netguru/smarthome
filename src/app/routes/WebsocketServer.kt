package app.routes

import app.Session
import app.WebsocketServer
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText

import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.webSocket
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach

@UseExperimental(ObsoleteCoroutinesApi::class)
fun Route.websocketServerSetup(server: WebsocketServer){
    webSocket("/ws") {

        val session = call.sessions.get<Session>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }

        server.memberJoin(session.id, this)

        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    server.receivedMessage(session.id, frame.readText())
                }
            }
        } finally {
            server.memberLeft(session.id, this)
        }
    }
}