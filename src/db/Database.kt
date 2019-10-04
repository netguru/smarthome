package db


import app.*
import com.github.mjdbc.Db
import com.github.mjdbc.DbFactory
import com.github.mjdbc.DbPreparedStatement
import com.uchuhimo.konf.Config
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Database {

    private var db: Db? = null
    private var sensorSql: SensorSql? = null
    private var transformSql: TransformSql? = null
    private var eventSql: EventSql? = null
    private var versionSql: VersionSql? = null

    fun isConnected() = db != null

    private fun <T> doWhileConnected(block: () -> T): T {
        if (isConnected()) {
            return try {
                block()
            } catch (e: Exception) {
                throw IllegalStateException("db not connected $e")
            }
        } else {
            throw IllegalStateException("db not connected")
        }
    }

    fun connect(config: Config) {
        try {
            val source = HikariDataSource().apply {
                jdbcUrl = "jdbc:postgresql://${config[Server.dbUrl]}"
                username = config[Server.dbUser]
                password = config[Server.dbPass]
                minimumIdle = 1
                isAutoCommit = false
            }
            db = DbFactory.wrap(source)
                .apply {
                    registerMapper(SensorEntity::class.java, SensorMapper())
                    registerMapper(TransformEntity::class.java, TransformMapper())
                    registerMapper(EventEntity::class.java, EventMapper())
                    registerMapper(Version::class.java, VersionMapper())

                    sensorSql = attachSql(SensorSql::class.java)
                    transformSql = attachSql(TransformSql::class.java)
                    eventSql = attachSql(EventSql::class.java)
                    versionSql = attachSql(VersionSql::class.java)
                }
        } catch (e: Exception) {
            logger.error { "exception while starting DB: $e" }
        }
    }

    private val currentVersion = 1
    fun createTables() {
        doWhileConnected {
            val versionExists =
                db!!.execute { c ->
                    val statement = DbPreparedStatement<Boolean>(
                        c,
                        "SELECT EXISTS ( " +
                                "   SELECT 1 " +
                                "   FROM   pg_tables " +
                                "   WHERE  schemaname = 'public'" +
                                "   AND    tablename = 'version'" +
                                "   )"
                    )

                    val result = statement.executeQuery()
                    result.next()
                    result.getBoolean("exists")
                }

            val version = if (versionExists == null || !versionExists) {
                logger.debug { "TABLE VERSION DOES NOT EXITS, RECREATING" }
                versionSql!!.create()
                versionSql!!.insert(Version(currentVersion))
                sensorSql!!.create()
                transformSql!!.create()
                eventSql!!.create()
                currentVersion
            } else {
                versionSql!!.getVersion().version
            }
            logger.debug { "Database version = $version" }

            if (version < currentVersion) {
                updateTables(version, currentVersion)
            }
        }
    }

    private fun updateTables(version: Int, newVersion: Int) {
        //implement this when db changes
    }

    //TODO: refactor to do select join transforms
    fun getSensor(id: Int): SensorResp {
        return doWhileConnected {
            val result = sensorSql!!.getSensor(id)
            result.toResp(transformSql!!.getAllForSensor(id))
        }
    }


    //TODO: refactor to do select join transforms
    fun getAllSensors(): List<SensorResp> {
        return doWhileConnected {
            val result = sensorSql!!.getAllSensors()
            result.map {
                it.toResp(transformSql!!.getAllForSensor(it.id))
            }.toList()
        }
    }

    fun modifySensor(id: Int?, sensorData: AddSensorReq): SensorResp {
        return doWhileConnected {
            val sensorId = if (id == null) {
                sensorSql!!.insertSensor(sensorData)
            } else {
                sensorSql!!.update(id, sensorData.name)
                id
            }

            addTransforms(sensorId, sensorData.transforms)
            removeTransforms(sensorData.transforms)
            modifyTransforms(sensorData.transforms)

            getSensor(sensorId)
        }

    }

    fun removeSensor(id: Int) {
        doWhileConnected {
            sensorSql!!.removeSensor(id)
        }
    }

    private fun addTransforms(sensorId: Int, transforms: List<TransformReq>): List<Int> {
        return doWhileConnected {
            val addTransforms = transforms.filter { it.action == TransformAction.ADD }
            if (addTransforms.isNotEmpty()) {
                return@doWhileConnected transformSql!!.insertBulk(
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
                            it.cmdTopic
                        )
                    }
                )
            }
            emptyList()
        }

    }

    private fun removeTransforms(transforms: List<TransformReq>) {
        return doWhileConnected {
            val toRemove = transforms
                .filter { it.action == TransformAction.REMOVE && it.id != null }
            if (toRemove.isNotEmpty()) {
                transformSql!!.remove(toRemove.map { it.id!! })
            }
        }
    }

    private fun modifyTransforms(transforms: List<TransformReq>) {
        doWhileConnected {
            val toModify = transforms
                .filter { it.action == TransformAction.UPDATE && it.id != null }

            if (toModify.isNotEmpty()) {
                transformSql!!.modify(toModify)
            }
        }
    }

    fun saveEvent(sensorId: Int, transformed: String, transformId: Int) {
        doWhileConnected {
            eventSql!!.insert(sensorId, transformId, transformed)
        }
    }

    fun getEventsForTransform(transformId: Int, limit: Int): List<EventEntity> {
        return doWhileConnected {
            eventSql!!.getForTransform(transformId, limit)
        }
    }

}