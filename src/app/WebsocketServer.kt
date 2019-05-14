package app

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import kotlinx.coroutines.channels.ClosedSendChannelException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class WebsocketServer {

    val members = ConcurrentHashMap<String, MutableList<WebSocketSession>>()

    fun memberJoin(id: String, session: WebSocketSession) {
        val list = members.computeIfAbsent(id) { CopyOnWriteArrayList<WebSocketSession>() }
        list.add(session)
    }

    fun memberLeft(id: String, session: WebSocketSession) {
        val connections = members[id]
        connections?.remove(session)

        if (connections != null && connections.isEmpty()) {
            members.remove(id)
        }
    }

    fun receivedMessage(id: String, readText: String) {
        //do nothing for now
    }

    suspend fun broadcast(message: String) {
        members.values.forEach { socket ->
            socket.send(Frame.Text(message))
        }
    }

    suspend fun List<WebSocketSession>.send(frame: Frame) {
        forEach {
            try {
                it.send(frame.copy())
            } catch (t: Throwable) {
                try {
                    it.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, ""))
                } catch (ignore: ClosedSendChannelException) {
                    // at some point it will get closed
                }
            }
        }
    }

    companion object {
        const val REFRESH_MESSAGE = "REFRESH"
    }

}