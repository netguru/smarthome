package com.netguru

data class AddSensorReq(
    val name: String,
    val topic: String,
    val transforms: List<TransformReq>)

data class TransformReq(
    val id: Int?, //id might be there if REMOVE or UPDATE ,
    val name: String?,
    val transform: String,
    val returnType: TransformReturnType,
    val action: TransformAction,
    val icon: String?
) {
    fun toTransform(id: Int, sensorId: Int) = Transform(id, sensorId, name, transform, returnType, icon)
}

enum class TransformAction {
    ADD, REMOVE, UPDATE
}