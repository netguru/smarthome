package app

import com.netguru.db.TransformEntity

data class AddSensorReq(
    val name: String,
    val topic: String,
    val transforms: List<TransformReq>)

data class TransformReq(
    val id: Int?,
    val name: String?,
    val transform: String,
    val returnType: String,
    val action: TransformAction,
    val icon: String?,
    val writable: Boolean
)

enum class TransformAction {
    ADD, REMOVE, UPDATE
}

data class SensorResp(
    val id: Int,
    val name: String,
    val topic: String,
    val transforms: List<TransformEntity>
)

data class EventReq(
    val sensorId: Int,
    val data: String,
    val transformId: Int
)

data class SettingsReq (
    val dbUrl: String,
    val dbUser: String,
    val dbPass: String,
    val mqttUrl: String,
    val mqttUser: String,
    val mqttPass: String
)