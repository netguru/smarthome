package com.netguru

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.RowData
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import java.time.LocalDateTime


enum class TransformReturnType {
    BOOLEAN, STRING, INT, FLOAT;

    companion object {
        fun of(value: String) = valueOf(value.toUpperCase())
    }
}

data class Sensor(
    val id: Int,
    val name: String,
    val topic: String,
    val transform: String,
    val returnType: TransformReturnType
)

data class Event(
    val id: Int,
    val sensorId: Int,
    val timeStamp: LocalDateTime,
    val data: String
)

class Database(private val connection: Connection) {

    fun createTables() {
        //TODO:
        // 1. check if table version exists
        // 2. check if table version row is lower than hardcoded
        // 3. update db according to migration functions
    }

    suspend fun addSensor(sensor: AddSensorReq): Sensor {
        val result = connection.sendPreparedStatementAwait(
            "INSERT INTO SENSORS (NAME, TOPIC, TRANSFORM, RETURN_TYPE) VALUES (?,?,?,?) RETURNING id",
            sensor.toValuesArray()
        )
        return Sensor(
            result.rows[0].getInt("id") ?: 0,
            sensor.name,
            sensor.topic,
            sensor.transform,
            TransformReturnType.of(sensor.returnType)
        )
    }

    suspend fun getAllSensors(): List<Sensor> {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM SENSORS ORDER BY id ASC")
        return result.rows.map {
            Sensor(
                it.getInt("id") ?: 0,
                it.getString("name") ?: "",
                it.getString("topic") ?: "",
                it.getString("transform") ?: "",
                TransformReturnType.of(it.getString("return_type") ?: "boolean")
            )
        }.toList()
    }


    suspend fun removeSensor(id: Int) {
        connection.sendPreparedStatementAwait("DELETE FROM SENSORS WHERE id = ? ", listOf(id))
    }

    suspend fun saveEvent(id: Int, transformed: String) {
        connection.sendPreparedStatementAwait(
            "INSERT INTO events (sensor_id, timestamp, data) VALUES (?, now(), ?)",
            listOf(id, transformed)
        )
    }

    suspend fun getEvents(id: Int, limit: Int): List<Event> {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM EVENTS WHERE sensor_id=? ORDER BY timestamp DESC LIMIT ?", listOf(id,limit))
        return result.rows.map {
            Event(
                it.getInt("id") ?: 0,
                it.getInt("sensor_id") ?: 0,
                it.getDate("timestamp").toJavaLocalDateTime(),
                it.getString("data") ?: ""
            )
        }
    }

    suspend fun getSensor(id: Int): Sensor = coroutineScope {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM SENSORS WHERE id = ? LIMIT 1", listOf(id))
        result.rows.map {
            it.toSensor()
        }.first()
    }

    suspend fun modifySensor(id: Int, sensorData: AddSensorReq): Sensor {
        connection.sendPreparedStatementAwait("UPDATE SENSORS SET (name, topic, transform,  return_type) = (?,?,?,?) WHERE id=$id", sensorData.toValuesArray())
        return Sensor(id, sensorData.name, sensorData.topic, sensorData.transform, TransformReturnType.of(sensorData.returnType))
    }

}

fun RowData.toSensor()=Sensor(
        getInt("id") ?: 0,
        getString("name") ?: "",
        getString("topic") ?: "",
        getString("transform") ?: "",
        TransformReturnType.of(getString("return_type") ?: "boolean")
    )

fun org.joda.time.LocalDateTime?.toJavaLocalDateTime(): LocalDateTime {
    return if (this == null) LocalDateTime.now()
    else
        LocalDateTime.of(
            year,
            monthOfYear,
            dayOfMonth,
            hourOfDay,
            minuteOfHour,
            secondOfMinute,
            millisOfSecond * 1_000_000
        )
}

suspend fun Connection.sendPreparedStatementAwait(query: String, values: List<Any> = emptyList()): QueryResult =
    this.sendPreparedStatement(query, values).await()