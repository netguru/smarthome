package di

import app.Server
import app.WebsocketServer
import com.github.mjdbc.DbFactory
import mqtt.MqttClient
import mqtt.MqttWorker
import com.uchuhimo.konf.Config
import com.zaxxer.hikari.HikariDataSource
import db.*
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.koin.core.qualifier.named
import org.koin.dsl.module


val DbModule = module {
    single {
        val config = get<Config>()
        val source = HikariDataSource().apply {
            jdbcUrl = "jdbc:postgresql://${config[Server.dbUrl]}"
            username = config[Server.dbUser]
            password = config[Server.dbPass]
            minimumIdle = 1
            isAutoCommit = false
        }
        DbFactory.wrap(source).apply {
            registerMapper(SensorEntity::class.java, SensorMapper())
            registerMapper(TransformEntity::class.java, TransformMapper())
            registerMapper(EventEntity::class.java, EventMapper())
            registerMapper(Version::class.java, VersionMapper())
        }
    }
}

val MainModule = module {
    single {
        val config = get<Config>()
        MqttClient(MqttAsyncClient(config[Server.mqttUrl], "ktor-server"))
    }
    single { Database(get()) }
    single { MqttWorker(get(), get(), get(), get()) }
    single { WebsocketServer() }
}

val ConfigModule = module {
    single {
        Config { addSpec(Server) }
            .from.json.resource("config.json")
            .from.json.file(get<String>(named("defaultConfigPath")))
            .from.env()
    }

    single(named("defaultConfigPath")) { "./config.json" }
}