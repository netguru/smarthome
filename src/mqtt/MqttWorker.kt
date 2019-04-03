package mqtt

import app.EventReq
import app.SensorResp
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.netguru.db.Database
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging

sealed class WorkerCmd {
    data class Subscribe(val sensor: SensorResp) : WorkerCmd()
    data class Unsubscribe(val id: Int) : WorkerCmd()
    data class PostEvent(val event: EventReq) : WorkerCmd()
}

private val logger = KotlinLogging.logger {}

class MqttWorker(
    private val mqttClient: MqttClient,
    private val db: Database
) {

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

    suspend fun doWork() = coroutineScope {
        //TODO: refactor user / pass to some env variable / pass store
        mqttClient.connect("pi", "spitulis")
        logger.debug { "mqtt connected" }

        launch {
            for (command in subscribeChannel) {
                when (command) {
                    is WorkerCmd.Subscribe -> {
                        launch {
                            logger.debug { "subscribing to sensor at topic ${command.sensor.topic}" }
                            for (message in mqttClient.subscribe(command.sensor.topic)) {
                                logger.debug { "message in ${command.sensor.topic} = $message" }
                                command.sensor.transforms.forEach {
                                    val data =  try { transform(message, it.transform, it.returnType) }
                                    catch (e: PathNotFoundException){
                                        null
                                    }
                                    if(data != null){
                                        db.saveEvent(
                                            command.sensor.id,
                                            data,
                                            it.id
                                        )
                                    }
                                }

                                //TODO: add posting event to refresh using websocket
                            }
                            logger.debug { "closing coroutine for ${command.sensor.id} topic: ${command.sensor.topic}" }
                        }
                    }
                    is WorkerCmd.Unsubscribe -> {
                        launch {
                            val sensor = db.getSensor(command.id)
                            mqttClient.unsubscribe(sensor.topic)
                        }
                    }
                    is WorkerCmd.PostEvent -> {
                        launch {
                            val sensor = db.getSensor(command.event.sensorId)

                            val transform = sensor.transforms.first { it.id == command.event.transformId }

                            val field = transform.transform
                                .split(".")
                                .last()

                            val json = if (transform.returnType == "STRING") {
                                "{\"$field\": \"${command.event.data}\" }"
                            } else {
                                "{\"$field\": ${command.event.data} }"
                            }
                            logger.debug { "publishing to ${sensor.topic} value: $json" }
                            mqttClient.publish(sensor.topic, json)
                            //TODO: add posting event to refresh using websocket
                        }
                    }
                }

            }
        }

        //subscribe to all sensors at startup
        db.getAllSensors().forEach {
            subscribeChannel.send(WorkerCmd.Subscribe(it))
        }
    }

    private fun transform(data: String, pattern: String, returnType: String): String {
        return when (returnType) {
            "BOOLEAN" -> JsonPath.parse(data).read<Boolean>(pattern).toString()
            "STRING" -> JsonPath.parse(data).read<String>(pattern).toString()
            "INT" -> JsonPath.parse(data).read<Int>(pattern).toString()
            "FLOAT" -> JsonPath.parse(data).read<Float>(pattern).toString()
            else -> throw IllegalArgumentException("Cound not find return type")
        }
    }
}