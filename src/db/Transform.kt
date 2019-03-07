package com.netguru.db

import com.github.mjdbc.Bind
import com.github.mjdbc.BindBean
import com.github.mjdbc.DbMapper
import com.github.mjdbc.Sql
import com.netguru.TransformReq
import java.sql.ResultSet

interface TransformSql {
    @Sql("INSERT INTO TRANSFORMS (transform, sensor_id, return_type, name, icon, writable) " +
            "VALUES (:transform, :sensorId, :returnType, :name, :icon, :writable) " +
            "RETURNING id")
    fun insertBulk(@BindBean transforms: List<TransormInsertReq>): List<Int>

    @Sql("SELECT * FROM TRANSFORMS WHERE sensor_id = :sensor_id ORDER BY id")
    fun getAllForSensor(@Bind("sensor_id") sensorId: Int): List<TransformEntity>

    @Sql("DELETE FROM TRANSFORMS WHERE id = :id")
    fun remove(@Bind("id") ids: List<Int>)

    @Sql("UPDATE TRANSFORMS SET (transform, return_type, name, icon, writable) " +
            "= (:transform, :returnType, :name, :icon, :writable) WHERE id=:id")
    fun modify(@BindBean transforms: List<TransformReq>)
}

data class TransformEntity(
    val id: Int,
    val sensorId : Int,
    val name:String?,
    val transform:String,
    val returnType:String,
    val icon:String?,
    val writable: Boolean
)

data class TransormInsertReq (
    val sensorId : Int,
    val name:String?,
    val transform:String,
    val returnType:String,
    val icon:String?,
    val writable: Boolean
)

class TransformMapper: DbMapper<TransformEntity> {
    override fun map(r: ResultSet): TransformEntity {
        return TransformEntity(
            r.getInt("id"),
            r.getInt("sensor_id"),
            r.getString("name"),
            r.getString("transform"),
            r.getString("return_type"),
            r.getString("icon"),
            r.getBoolean("writable")
            )
    }

}