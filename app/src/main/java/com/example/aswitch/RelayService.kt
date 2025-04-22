package com.example.aswitch

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class RelayService : NetworkUtils.OnIpChangedListener {
    companion object {
        private const val TAG = "RelayService"
    }

    private var baseUrl: String = NetworkUtils.getBaseUrl()

    init {
        NetworkUtils.addIpChangeListener(this)
    }

    override fun onIpChanged(newIp: String) {
        baseUrl = newIp
    }

    suspend fun controlRelay(relayNumber: Int, turnOn: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val action = if (turnOn) "on" else "off"
            val url = URL("$baseUrl/relay$relayNumber/$action")
            Log.d(TAG, "Sending request to: $url")
            
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            Log.d(TAG, "Relay $relayNumber $action response: $responseCode")
            
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Failed to control relay $relayNumber. Response code: $responseCode")
                return@withContext false
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error controlling relay $relayNumber: ${e.message}")
            false
        }
    }
} 