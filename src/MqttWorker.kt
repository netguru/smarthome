package com.netguru

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger

class MqttWorker(
    private val mqttClient: MqttClient,
    private val db: Database,
    private val subscribeChannel: Channel<Sensor>,
    private val log: Logger
) {

    suspend fun doWork() = coroutineScope {
        //TODO: refactor user / pass to some env variable / pass store
        mqttClient.connect("pi", "spitulis")
        log.debug("mqtt connected")

        log.debug("before for loop")

        launch {
            for ( sensor in subscribeChannel){
                launch {
                    log.debug("subscribing to sensor at topic ${sensor.topic}")
                    for( message in  mqttClient.subscribe(sensor.topic)) {
                        log.debug("message in ${sensor.topic} = $message")
                        db.saveEvent(sensor.id, transform(message, sensor.transform, sensor.returnType))
                        //TODO: add posting event to refresh using websocket
                    }
                }
            }
        }
        log.debug("after for loop")

        //subscribe to all sensors at startup
        db.getAllSensors().forEach {
            subscribeChannel.send(it)
        }
    }
}