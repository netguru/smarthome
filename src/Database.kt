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
    val transforms: List<Transform>
)

data class Transform(
    val id: Int,
    val sensorId: Int,
    val name: String?,
    val transform: String,
    val returnType: TransformReturnType
)

data class Event(
    val id: Int,
    val sensorId: Int,
    val timeStamp: LocalDateTime,
    val data: String,
    val transformId: Int,
    val transformName: String
)

//TODO: check https://github.com/JetBrains/Exposed for ORM
class Database(private val connection: Connection) {

    fun createTables() {
        //TODO:
        // 1. check if table version exists
        // 2. check if table version row is lower than hardcoded
        // 3. update db according to migration functions
    }

    suspend fun addSensor(sensor: AddSensorReq): Sensor = coroutineScope {
        val result = connection.sendPreparedStatementAwait(
            "INSERT INTO SENSORS (NAME, TOPIC) VALUES (?,?) RETURNING id",
            listOf(sensor.name, sensor.topic)
        )

        val sensorId = result.rows[0].getInt("id")!!
        val transformIds = addTransforms(sensorId, sensor.transforms)
        Sensor(
            sensorId,
            sensor.name,
            sensor.topic,
            sensor.transforms.mapIndexed { index, transformReq ->
                Transform(
                    transformIds[index],
                    sensorId,
                    transformReq.name,
                    transformReq.transform,
                    transformReq.returnType
                )
            }
        )
    }

    private suspend fun addTransforms(sensorId: Int, transforms: List<TransformReq>): List<Int> {
        val addTransforms = transforms.filter { it.action == TransformAction.ADD }
        if (addTransforms.isNotEmpty()) {
            val query = addTransforms
                .joinToString(
                    separator = ",",
                    prefix = "INSERT INTO TRANSFORMS (transform, sensor_id, return_type, name) VALUES ",
                    postfix = " RETURNING id"
                ) {
                    "( \'${it.transform}\', $sensorId, \'${it.returnType}\', \'${it.name}\')"
                }

            val resultTransform = connection.sendPreparedStatementAwait(query)
            return resultTransform.rows.map { it.getInt("id")!! }
        }

        return emptyList()
    }

    suspend fun getAllSensors(): List<Sensor> {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM SENSORS ORDER BY id ASC")
        return result.rows.map {
            //get transform for sensor
            val id = it.getInt("id")!!
            it.toSensor(getTransforms(id))
        }.toList()
    }

    suspend fun getTransforms(sensorId: Int): List<Transform> {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM TRANSFORMS WHERE sensor_id = $sensorId")
        return result.rows.map {
            Transform(
                it.getInt("id")!!,
                it.getInt("sensor_id")!!,
                it.getString("name"),
                it.getString("transform")!!,
                TransformReturnType.of(it.getString("return_type")!!)
            )
        }
    }


    suspend fun removeSensor(id: Int) {
        connection.sendPreparedStatementAwait("DELETE FROM SENSORS WHERE id = ? ", listOf(id))
    }

    suspend fun saveEvent(id: Int, transformed: String, transformId: Int, transformName: String) {
        connection.sendPreparedStatementAwait(
            "INSERT INTO events (sensor_id, timestamp, data, transform_id, transform_name) VALUES (?, now(), ?,?,?)",
            listOf(id, transformed, transformId, transformName)
        )
    }

    suspend fun getEvents(id: Int, limit: Int): List<Event> {
        val result = connection.sendPreparedStatementAwait(
            "SELECT * FROM EVENTS WHERE sensor_id=? ORDER BY timestamp DESC LIMIT ?",
            listOf(id, limit)
        )
        return result.rows.map {
            Event(
                it.getInt("id")!!,
                it.getInt("sensor_id")!!,
                it.getDate("timestamp").toJavaLocalDateTime(),
                it.getString("data")!!,
                it.getInt("transform_id")!!,
                it.getString("transform_name")!!
            )
        }
    }

    suspend fun getSensor(id: Int): Sensor = coroutineScope {
        val result = connection.sendPreparedStatementAwait("SELECT * FROM SENSORS WHERE id = ? LIMIT 1", listOf(id))
        result.rows.map {
            it.toSensor(getTransforms(id))
        }.first()
    }

    suspend fun modifySensor(id: Int, sensorData: AddSensorReq): Sensor = coroutineScope {
        connection.sendPreparedStatementAwait(
            "UPDATE SENSORS SET (name, topic) = (\'${sensorData.name}\', \'${sensorData.topic}\') WHERE id=$id"
        )

        addTransforms(id, sensorData.transforms)
        removeTransforms(sensorData.transforms)
        modifyTransforms(sensorData.transforms)

        Sensor(
            id,
            sensorData.name,
            sensorData.topic,
            getTransforms(id)
        )
    }

    private suspend fun removeTransforms(transforms: List<TransformReq>) {
        val toRemove = transforms
            .filter { it.action == TransformAction.REMOVE }
        if (toRemove.isNotEmpty()) {
            val ids = toRemove
                .map { it.id!! }
                .joinToString(",", prefix = "(", postfix = ")") { it.toString() }

            connection.sendPreparedStatementAwait("DELETE FROM TRANSFORMS WHERE id IN $ids")
        }
    }

    private suspend fun modifyTransforms(transforms: List<TransformReq>) {
        transforms.filter { it.action == TransformAction.UPDATE }
            .forEach {
                connection.sendPreparedStatementAwait(
                    "UPDATE TRANSFORMS SET (transform, return_type, name)" +
                            " = (?, ?, ?) WHERE id=${it.id}",
                    listOf(it.transform, it.returnType.name , it.name?:"")
                )
            }
    }

}

fun RowData.toSensor(transforms: List<Transform>) = Sensor(
    getInt("id")!!,
    getString("name")!!,
    getString("topic")!!,
    transforms
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