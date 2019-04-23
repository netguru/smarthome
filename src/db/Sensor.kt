package com.netguru.db

import com.github.mjdbc.Bind
import com.github.mjdbc.BindBean
import com.github.mjdbc.DbMapper
import com.github.mjdbc.Sql
import app.AddSensorReq
import app.SensorResp
import java.sql.ResultSet


data class SensorEntity (
    val id: Int,
    val name: String
)

interface SensorSql {
    @Sql("INSERT INTO sensors (name) VALUES (:name) RETURNING id")
    fun insertSensor(@BindBean sensor: AddSensorReq): Int

    @Sql("SELECT * FROM SENSORS ORDER BY id ASC")
    fun getAllSensors(): List<SensorEntity>

    @Sql("SELECT * FROM SENSORS WHERE id = :id")
    fun getSensor(@Bind("id") id: Int): SensorEntity

    @Sql("DELETE FROM SENSORS WHERE id = :id")
    fun removeSensor(@Bind("id") id: Int)

    @Sql("UPDATE SENSORS SET (name) = (:name) WHERE id=:id")
    fun update(@Bind("id") id: Int, @Bind("name") name: String)
}

class SensorMapper : DbMapper<SensorEntity> {
    override fun map(r: ResultSet): SensorEntity {
        return SensorEntity(
            r.getInt("id"),
            r.getString("name")
            )
    }
}

fun SensorEntity.toResp(transforms: List<TransformEntity>) = SensorResp(id, name, transforms)
