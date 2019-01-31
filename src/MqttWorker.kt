package com.netguru

import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging

sealed class WorkerCmd {
    data class Subscribe(val sensor: Sensor) : WorkerCmd()
    data class Unsubscribe(val id: Int) : WorkerCmd()
}

private val logger = KotlinLogging.logger {}

class MqttWorker(
    private val mqttClient: MqttClient,
    private val db: Database,
    private val subscribeChannel: Channel<WorkerCmd>
) {

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
                                val transforms = db.getTransforms(command.sensor.id)
                                transforms.forEach {
                                    db.saveEvent(
                                        command.sensor.id,
                                        transform(message, it.transform, it.returnType),
                                        it.id,
                                        it.name ?: ""
                                    )
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
                }

            }
        }

        //subscribe to all sensors at startup
        db.getAllSensors().forEach {
            subscribeChannel.send(WorkerCmd.Subscribe(it))
        }
    }

    private fun transform(data: String, pattern: String, returnType: TransformReturnType): String {
        return when (returnType) {
            TransformReturnType.BOOLEAN -> JsonPath.parse(data).read<Boolean>(pattern).toString()
            TransformReturnType.STRING -> JsonPath.parse(data).read<String>(pattern).toString()
            TransformReturnType.INT -> JsonPath.parse(data).read<Int>(pattern).toString()
            TransformReturnType.FLOAT -> JsonPath.parse(data).read<Float>(pattern).toString()
        }
    }
}