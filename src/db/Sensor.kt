package com.netguru.db

import com.github.mjdbc.Bind
import com.github.mjdbc.BindBean
import com.github.mjdbc.DbMapper
import com.github.mjdbc.Sql
import com.netguru.AddSensorReq
import com.netguru.SensorResp
import java.sql.ResultSet


data class SensorEntity (
    val id: Int,
    val name: String,
    val topic: String
)

interface SensorSql {
    @Sql("INSERT INTO sensors (name, topic) VALUES (:name, :topic) RETURNING id")
    fun insertSensor(@BindBean sensor: AddSensorReq): Int

    @Sql("SELECT * FROM SENSORS ORDER BY id ASC")
    fun getAllSensors(): List<SensorEntity>

    @Sql("SELECT * FROM SENSORS WHERE id = :id")
    fun getSensor(@Bind("id") id: Int): SensorEntity

    @Sql("DELETE FROM SENSORS WHERE id = :id")
    fun removeSensor(@Bind("id") id: Int)

    @Sql("UPDATE SENSORS SET (name, topic) = (:name, :topic) WHERE id=:id")
    fun update(@Bind("id") id: Int, @Bind("name") name: String, @Bind("topic") topic: String)
}

class SensorMapper : DbMapper<SensorEntity> {
    override fun map(r: ResultSet): SensorEntity {
        return SensorEntity(
            r.getInt("id"),
            r.getString("name"),
            r.getString("topic")
            )
    }
}

fun SensorEntity.toResp(transforms: List<TransformEntity>) = SensorResp(id, name, topic, transforms)
