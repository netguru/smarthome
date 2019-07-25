package di

import app.Server
import app.WebsocketServer
import com.uchuhimo.konf.Config
import db.Database
import mqtt.MqttClient
import mqtt.MqttWorker
import org.koin.core.qualifier.named
import org.koin.dsl.module


val MainModule = module {
    single {
        MqttClient(get())
    }
    single { Database() }
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