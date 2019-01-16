package com.netguru

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient as PahoClient

class MqttClient(private val client: PahoClient) {

    private val channelsMap = HashMap<String, Channel<String>>()

    suspend fun connect(user: String, pass: String) = suspendCancellableCoroutine<IMqttToken> {
        client.connect(MqttConnectOptions().apply {
            userName = user
            password = pass.toCharArray()
        }, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                it.resume(asyncActionToken)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
                it.resumeWithException(exception)
            }
        })
    }

    //    suspend fun publish(topic: String, message: String) = coroutineScope {
//
//    }
//
    suspend fun subscribe(topic: String)  = suspendCancellableCoroutine<Channel<String>> {
        val channel = channelsMap[topic] ?: Channel()

        client.subscribe(topic,0) { _, message ->
            channel.offer("$message")
        }.actionCallback = object : IMqttActionListener {
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
                channel.close()
                it.resumeWithException(exception)
            }

            override fun onSuccess(asyncActionToken: IMqttToken?) {
                channelsMap[topic] = channel
                it.resume(channel)
            }
        }
    }

    suspend fun unsubscribe(topic: String) = withContext(Dispatchers.IO) {
        channelsMap[topic]?.close()
        client.unsubscribe(topic)
    }

    companion object {
        const val ALL_TOPICS = "#"
    }
}
