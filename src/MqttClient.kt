package com.netguru

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient as PahoClient

class MqttClient(private val client: PahoClient) {

    private val channelsMap = HashMap<String, Channel<String>>()

    suspend fun connect(user: String, pass: String) = suspendCancellableCoroutine<Unit> {
        client.connect(MqttConnectOptions().apply {
            userName = user
            password = pass.toCharArray()
        }, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                it.resume(Unit)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
                it.resumeWithException(exception)
            }
        })
    }

    suspend fun publish(topic: String, message: String) = suspendCancellableCoroutine<IMqttToken> {
        client.publish(topic, MqttMessage(message.toByteArray()), null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                if(asyncActionToken != null){
                    it.resume(asyncActionToken)
                } else {
                    it.resumeWithException(NullPointerException("publish success but  with no Token"))
                }
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                if(exception != null){
                    it.resumeWithException(exception)
                } else {
                    it.resumeWithException(NullPointerException("publish failed with no exception"))
                }

            }

        })
    }

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
