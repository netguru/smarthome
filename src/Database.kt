package com.netguru

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await

data class Sensor(
    val id: Int,
    val name: String,
    val topic: String,
    val transform: String
)

class Database(private val connection: Connection) {

    fun createTables() {
        //TODO
    }

    suspend fun addSensor(sensor: AddSensorReq) {
        connection.sendPreparedStatementAwait(
            "INSERT INTO SENSORS (NAME, TOPIC, TRANSFORM) VALUES (?,?,?)",
            sensor.toValuesArray()
        )
    }

    suspend fun getAllSensors(): List<Sensor> = coroutineScope {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM SENSORS")
        result.rows.map {
            Sensor(
                it.getInt("id") ?: 0,
                it.getString("name") ?: "",
                it.getString("topic") ?: "",
                it.getString("transform") ?: ""
            )
        }.toList()
    }

    fun removeAll() {

    }

    fun removeSensor(id: Int) {

    }

    fun removeByTopic(topic: String) {

    }
}

suspend fun Connection.sendPreparedStatementAwait(query: String, values: List<Any> = emptyList()): QueryResult =
    this.sendPreparedStatement(query, values).await()