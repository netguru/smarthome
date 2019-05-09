package db

import com.github.mjdbc.Bind
import com.github.mjdbc.DbMapper
import com.github.mjdbc.Sql
import java.sql.ResultSet
import java.time.LocalDateTime

data class EventEntity(

    val id: Int,
    val sensorId: Int,
    val timeStamp: LocalDateTime,
    val data: String,
    val transformId: Int
)

class EventMapper : DbMapper<EventEntity> {
    override fun map(r: ResultSet): EventEntity {
        return EventEntity(
            r.getInt("id"),
            r.getInt("sensor_id"),
            r.getTimestamp("timestamp").toLocalDateTime(),
            r.getString("data"),
            r.getInt("transform_id")
        )
    }

}

interface EventSql {
    @Sql(
        "INSERT INTO events (sensor_id, timestamp, data, transform_id) VALUES " +
                "(:id, now(), :transformed, :transform_id)"
    )
    fun insert(@Bind("id") id: Int, @Bind("transform_id") transformId: Int, @Bind("transformed") transformed: String)

    @Sql( "SELECT * FROM EVENTS WHERE transform_id=:id ORDER BY timestamp DESC LIMIT :limit")
    fun getForTransform(@Bind("id") transformId: Int, @Bind("limit") limit: Int): List<EventEntity>

    @Sql("CREATE TABLE events ( " +
            "id serial NOT NULL PRIMARY KEY, " +
            "data varchar NOT NULL, " +
            "timestamp timestamp NOT NULL, " +
            "sensor_id int NOT NULL, " +
            "transform_id int NOT NULL, " +
            "CONSTRAINT events_sensor_id_fkey FOREIGN KEY (sensor_id) REFERENCES sensors(id) ON DELETE CASCADE, " +
            "CONSTRAINT events_transform_id_fkey FOREIGN KEY (transform_id) REFERENCES transforms(id) ON DELETE CASCADE " +
            ")")
    fun create()

}

