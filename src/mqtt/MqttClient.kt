package mqtt

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.suspendCancellableCoroutine
import mu.KotlinLogging
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient as PahoClient

private val logger = KotlinLogging.logger {}

class MqttClient(private val client: PahoClient) {

    private val channelsMap = HashMap<String, Channel<String>>()

    fun isConnected() = client.isConnected

    suspend fun connect(user: String, pass: String) = suspendCancellableCoroutine<Unit> {
        logger.debug { "connecting user: $user" }
        client.connect(MqttConnectOptions().apply {
            userName = user
            password = pass.toCharArray()
        }, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                logger.debug { "OK" }
                it.resume(Unit)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
                logger.debug { "Error" }
                it.resumeWithException(exception)
            }
        })
    }

    suspend fun publish(topic: String, message: String) = suspendCancellableCoroutine<Unit> {
        client.publish(topic, MqttMessage(message.toByteArray()), null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                if(asyncActionToken != null && asyncActionToken.isComplete){
                    it.resume(Unit)
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

        client.subscribe(topic,0, null, object : IMqttActionListener {
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
                logger.debug { "failed subcription to $topic with $exception" }
                channelsMap[topic]?.close()
                it.resumeWithException(exception)
            }

            override fun onSuccess(asyncActionToken: IMqttToken?) {
                val channel = Channel<String>()
                channelsMap[topic] = channel
                it.resume(channel)
            }
        }) { _, message ->
            channelsMap[topic]?.offer("$message")
        }
    }

    suspend fun unsubscribe(topic: String) = suspendCancellableCoroutine<Unit> {
        channelsMap[topic]?.close()
        channelsMap.remove(topic)
        client.unsubscribe(topic, null, object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                it.resume(Unit)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
                it.resumeWithException(exception)
            }
        })
    }

    suspend fun disconnect() = suspendCancellableCoroutine<Unit>{
        channelsMap.forEach { channel ->  channel.value.close() }
        channelsMap.clear()
        client.disconnect(null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                it.resume(Unit)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
                it.resumeWithException(exception)
            }

        })
    }
}
