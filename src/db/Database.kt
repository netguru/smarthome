package db


import app.AddSensorReq
import app.SensorResp
import app.TransformAction
import app.TransformReq
import com.github.mjdbc.Db
import com.github.mjdbc.DbPreparedStatement
import mu.KotlinLogging
import org.postgresql.util.PSQLException

private val logger = KotlinLogging.logger {}

class Database(private val db: Db) {


    private val sensorSql = db.attachSql(SensorSql::class.java)
    private val transformSql = db.attachSql(TransformSql::class.java)
    private val eventSql = db.attachSql(EventSql::class.java)
    private val versionSql = db.attachSql(VersionSql::class.java)

    private val currentVersion = 1
    fun createTables() {

        val versionExists =
            db.execute { c ->
                val statement = DbPreparedStatement<Boolean>(c,
                    "SELECT EXISTS ( "+
                        "   SELECT 1 " +
                        "   FROM   pg_tables " +
                        "   WHERE  schemaname = 'public'" +
                        "   AND    tablename = 'version'" +
                        "   )")

                val result = statement.executeQuery()
                result.next()
                result.getBoolean("exists")
            }

        val version = if(versionExists == null || !versionExists) {
            logger.debug { "TABLE VERSION DOES NOT EXITS, RECREATING" }
            versionSql.create()
            versionSql.insert(Version(currentVersion))
            sensorSql.create()
            transformSql.create()
            eventSql.create()
            currentVersion
        } else {
            versionSql.getVersion().version
        }
        logger.debug { "Database version = $version" }

        if (version < currentVersion) {
            updateTables(version, currentVersion)
        }
    }

    private fun updateTables(version: Int, newVersion: Int) {
        //implement this when db changes
    }

    //TODO: refactor to do select join transforms
    fun getSensor(id: Int): SensorResp {
        val result = sensorSql.getSensor(id)
        return result.toResp(transformSql.getAllForSensor(id))
    }

    //TODO: refactor to do select join transforms
    fun getAllSensors(): List<SensorResp> {
        val result = sensorSql.getAllSensors()
        return result.map {
            it.toResp(transformSql.getAllForSensor(it.id))
        }.toList()
    }

    fun modifySensor(id: Int?, sensorData: AddSensorReq): SensorResp {
        val sensorId = if (id == null) {
            sensorSql.insertSensor(sensorData)
        } else {
            sensorSql.update(id, sensorData.name)
            id
        }

        addTransforms(sensorId, sensorData.transforms)
        removeTransforms(sensorData.transforms)
        modifyTransforms(sensorData.transforms)

        return getSensor(sensorId)
    }

    fun removeSensor(id: Int) {
        sensorSql.removeSensor(id)
    }

    private fun addTransforms(sensorId: Int, transforms: List<TransformReq>): List<Int> {
        val addTransforms = transforms.filter { it.action == TransformAction.ADD }
        if (addTransforms.isNotEmpty()) {
            return transformSql.insertBulk(
                addTransforms.map {
                    TransformReq(
                        null,
                        sensorId,
                        it.name,
                        it.transform,
                        it.returnType,
                        TransformAction.ADD,
                        it.icon,
                        it.writable,
                        it.topic,
                        it.boolTrue,
                        it.boolFalse,
                        it.cmdTopic)
                }
            )
        }
        return emptyList()
    }

    private fun removeTransforms(transforms: List<TransformReq>) {
        val toRemove = transforms
            .filter { it.action == TransformAction.REMOVE && it.id != null }
        if (toRemove.isNotEmpty()) {
            transformSql.remove(toRemove.map { it.id!! })
        }
    }

    private fun modifyTransforms(transforms: List<TransformReq>) {
        val toModify = transforms
            .filter { it.action == TransformAction.UPDATE && it.id != null }

        if (toModify.isNotEmpty()) {
            transformSql.modify(toModify)
        }
    }

    fun saveEvent(sensorId: Int, transformed: String, transformId: Int) {
        eventSql.insert(sensorId, transformId, transformed)
    }

    fun getEventsForTransform(transformId: Int, limit: Int): List<EventEntity> {
        return eventSql.getForTransform(transformId, limit)
    }

}