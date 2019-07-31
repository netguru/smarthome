package mqtt

import app.EventReq
import app.SensorResp
import app.Server
import app.WebsocketServer
import app.WebsocketServer.Companion.REFRESH_MESSAGE
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import db.Database
import com.uchuhimo.konf.Config
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class MqttWorker(
    private val mqttClient: MqttClient,
    private val db: Database,
    private val config: Config,
    private val websocketServer: WebsocketServer
) {

    private sealed class WorkerCmd {
        data class Subscribe(val sensor: SensorResp) : WorkerCmd()
        data class Unsubscribe(val id: Int) : WorkerCmd()
        data class PostEvent(val event: EventReq) : WorkerCmd()
    }

    private val subscribeChannel = Channel<WorkerCmd>()

    suspend fun subscribe(sensor: SensorResp) = coroutineScope{
        subscribeChannel.send(WorkerCmd.Subscribe(sensor))
    }

    suspend fun unsubscribe(id: Int) = coroutineScope{
        subscribeChannel.send(WorkerCmd.Unsubscribe(id))
    }

    suspend fun postEvent(event: EventReq) = coroutineScope{
        subscribeChannel.send(WorkerCmd.PostEvent(event))
    }

    suspend fun connectAndRun() = coroutineScope {
        mqttClient.connect(config[Server.mqttUser], config[Server.mqttPass])
        logger.debug { "mqtt connected" }

        launch {
            for (command in subscribeChannel) {
                when (command) {
                    is WorkerCmd.Subscribe -> {
                        launch {
                            subscribeAction(command)
                        }
                    }
                    is WorkerCmd.Unsubscribe -> {
                        launch {
                            unsubscribeAction(command)
                        }
                    }
                    is WorkerCmd.PostEvent -> {
                        launch {
                            postEventAction(command)
                        }
                    }
                }
            }
        }

        //subscribe to all sensors at startup
        db.getAllSensors().forEach {
            subscribe(it)
        }
    }

    private suspend fun postEventAction(command: WorkerCmd.PostEvent) = coroutineScope {
        if(db.isConnected()) {
            val sensor = db.getSensor(command.event.sensorId)

            val transform = sensor.transforms.first { it.id == command.event.transformId }

            val field = transform.transform
                .split(".")
                .last()

            val json = when (transform.returnType) {
                "BOOLEAN" -> {
                    val onValue = transform.boolOn ?: "true"
                    val offValue = transform.boolOff ?: "false"
                    if (command.event.data == "true") {
                        onValue
                    } else {
                        offValue
                    }
                }
                "STRING" -> "{\"$field\": \"${command.event.data}\" }"

                else -> "{\"$field\": ${command.event.data} }"
            }
            logger.debug { "publishing to ${sensor.name} value: $json" }
            val topic = transform.cmdTopic ?: transform.topic
            mqttClient.publish(topic, json)
        }
    }

    private suspend fun unsubscribeAction(command: WorkerCmd.Unsubscribe) = coroutineScope {
        val sensor = db.getSensor(command.id)
        for (transform in sensor.transforms){
            mqttClient.unsubscribe(transform.topic)
        }
    }

    private suspend fun subscribeAction(command: WorkerCmd.Subscribe) = coroutineScope {
        logger.debug { "subscribing to sensor  ${command.sensor.name}" }

        for(topic in command.sensor.transforms.map { it.topic }.toSet()){

            logger.debug { "subscribing to topic  $topic" }
            launch {
                for(message in mqttClient.subscribe(topic)){
                    logger.debug { "message in $topic = $message" }

                    command.sensor.transforms.filter { it.topic == topic }.forEach {
                        val data = try {
                            transform(message, it.transform, it.returnType, it.boolOn, it.boolOff)
                        } catch (e: PathNotFoundException) {
                            null
                        }
                        if (data != null) {
                            db.saveEvent(
                                command.sensor.id,
                                data,
                                it.id
                            )

                            websocketServer.broadcast(REFRESH_MESSAGE)
                        }
                    }
                }
                logger.debug { "closing coroutine for ${command.sensor.id} topic: ${command.sensor.name}" }
            }
        }

    }

    private fun transform(
        data: String,
        pattern: String,
        returnType: String,
        boolOn: String?,
        boolOff: String?
    ): String {
        return when (returnType) {
            "BOOLEAN" -> {
                if(boolOn!= null && boolOff != null){
                    when(JsonPath.parse(data).read<String>(pattern)){
                        boolOn -> "true"
                        boolOff -> "false"
                        else -> "false"
                    }
                } else {
                    JsonPath.parse(data).read<Boolean>(pattern).toString()
                }

            }
            "STRING" -> JsonPath.parse(data).read<String>(pattern).toString()
            "INT" -> JsonPath.parse(data).read<Int>(pattern).toString()
            "FLOAT" -> JsonPath.parse(data).read<Float>(pattern).toString()
            else -> throw IllegalArgumentException("Cound not find return type")
        }
    }
}