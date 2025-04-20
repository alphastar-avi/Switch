package com.example.aswitch

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Appliance(
    val name: String,
    val type: ApplianceType,
    val startTime: String,
    val endTime: String,
    var isOn: Boolean
) {
    companion object {
        private val gson = Gson()
        
        fun toJson(appliance: Appliance): String {
            return gson.toJson(appliance)
        }
        
        fun fromJson(json: String): Appliance {
            return gson.fromJson(json, Appliance::class.java)
        }
        
        fun toJsonArray(appliances: List<Appliance>): String {
            return gson.toJson(appliances)
        }
        
        fun fromJsonArray(json: String): List<Appliance> {
            val type = object : TypeToken<List<Appliance>>() {}.type
            return gson.fromJson(json, type)
        }
    }
}

enum class ApplianceType {
    LIGHT,
    FAN,
    TV
} 