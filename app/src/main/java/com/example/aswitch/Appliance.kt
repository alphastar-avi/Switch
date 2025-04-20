package com.example.aswitch

data class Appliance(
    val name: String,
    val type: ApplianceType,
    val startTime: String,
    val endTime: String,
    var isOn: Boolean
)

enum class ApplianceType {
    LIGHT,
    FAN,
    TV
} 