package com.netguru

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

data class Sensor(
    val id: Int,
    val name: String,
    val topic: String,
    val transform: String
)

data class Event(
    val id: Int,
    val sensorId: Int,
    val timeStamp: LocalDateTime,
    val rawData: String,
    val data: String
)

class Database(private val connection: Connection) {

    fun createTables() {
        //TODO
    }

    suspend fun addSensor(sensor: AddSensorReq): Sensor {
        val result = connection.sendPreparedStatementAwait(
            "INSERT INTO SENSORS (NAME, TOPIC, TRANSFORM) VALUES (?,?,?) RETURNING id",
            sensor.toValuesArray()
        )
        return Sensor(result.rows[0].getInt("id")?:0, sensor.name, sensor.topic, sensor.transform)
    }

    suspend fun getAllSensors(): List<Sensor> {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM SENSORS")
        return result.rows.map {
            Sensor(
                it.getInt("id") ?: 0,
                it.getString("name") ?: "",
                it.getString("topic") ?: "",
                it.getString("transform") ?: ""
            )
        }.toList()
    }


    suspend fun removeSensor(id: Int) {
        connection.sendPreparedStatementAwait("DELETE FROM SENSORS WHERE id = ? ", listOf(id))
    }

    suspend fun saveEvent(id: Int, message: String, transformed: String) {
        connection.sendPreparedStatementAwait("INSERT INTO events (sensor_id, timestamp, raw_data, data) VALUES (?, now(), ?, ?", listOf(id, message, transformed))
    }

    suspend fun getEvents(id: Int): List<Event> {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM EVENTS WHERE sensor_id=?", listOf(id))
        return result.rows.map {
            Event(it.getInt("id")?:0,
                it.getInt("sensor_id")?:0,
                it.getDate("timestamp")?:LocalDateTime(),
                it.getString("raw_data")?:"",
                it.getString("data")?:"")
        }
    }

}

suspend fun Connection.sendPreparedStatementAwait(query: String, values: List<Any> = emptyList()): QueryResult =
    this.sendPreparedStatement(query, values).await()