package com.netguru

data class AddSensorReq(val name: String, val topic: String, val transform: String) {
    fun toValuesArray(): List<String> = listOf(name, topic, transform)
}