package com.example.aswitch

import android.content.Context
import android.content.SharedPreferences

object NetworkUtils {
    private var BASE_URL: String = "http://192.168.1.11"
    private lateinit var sharedPreferences: SharedPreferences
    private val ipChangeListeners = mutableListOf<OnIpChangedListener>()

    interface OnIpChangedListener {
        fun onIpChanged(newIp: String)
    }

    fun addIpChangeListener(listener: OnIpChangedListener) {
        ipChangeListeners.add(listener)
    }

    fun removeIpChangeListener(listener: OnIpChangedListener) {
        ipChangeListeners.remove(listener)
    }

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        BASE_URL = sharedPreferences.getString("esp32_ip", "http://192.168.1.11") ?: "http://192.168.1.11"
    }

    fun getBaseUrl(): String {
        return BASE_URL
    }

    fun updateBaseUrl(newIp: String) {
        BASE_URL = newIp
        sharedPreferences.edit().putString("esp32_ip", newIp).apply()
        // Notify all listeners about the IP change
        ipChangeListeners.forEach { it.onIpChanged(newIp) }
    }
} 