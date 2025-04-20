package com.example.aswitch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApplianceAlarmReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "ApplianceAlarmReceiver"
        private const val WAKELOCK_TAG = "ApplianceAlarmReceiver::Wakelock"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val applianceName = intent.getStringExtra("appliance_name") ?: return
        val action = intent.getStringExtra("action") ?: return
        val relayNumber = intent.getIntExtra("relay_number", 0)

        if (relayNumber == 0) {
            Log.e(TAG, "Invalid relay number for $applianceName")
            return
        }

        // Acquire a wake lock to ensure the device stays awake
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            WAKELOCK_TAG
        )
        wakeLock.acquire(10_000) // Hold wake lock for 10 seconds

        try {
            Log.d(TAG, "Alarm triggered for $applianceName: $action (relay $relayNumber)")
            
            val relayService = RelayService()
            val turnOn = action == "start"

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val success = relayService.controlRelay(relayNumber, turnOn)
                    if (success) {
                        Log.d(TAG, "Successfully ${if (turnOn) "turned on" else "turned off"} $applianceName (Relay $relayNumber)")
                        
                        // Update the appliance state in the database or shared preferences
                        val sharedPrefs = context.getSharedPreferences("appliances", Context.MODE_PRIVATE)
                        val appliancesJson = sharedPrefs.getString("appliances_list", "[]")
                        val appliances = Appliance.fromJsonArray(appliancesJson ?: "[]")
                        
                        val updatedAppliances = appliances.map { appliance ->
                            if (appliance.name == applianceName) {
                                appliance.copy(isOn = turnOn)
                            } else {
                                appliance
                            }
                        }
                        
                        sharedPrefs.edit().putString("appliances_list", Appliance.toJsonArray(updatedAppliances)).apply()
                        
                        // Send a broadcast to update the UI
                        val updateIntent = Intent("com.example.aswitch.RELAY_STATE_CHANGED").apply {
                            putExtra("appliance_name", applianceName)
                            putExtra("is_on", turnOn)
                        }
                        context.sendBroadcast(updateIntent)
                    } else {
                        Log.e(TAG, "Failed to ${if (turnOn) "turn on" else "turn off"} $applianceName (Relay $relayNumber)")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error controlling relay for $applianceName: ${e.message}")
                }
            }
        } finally {
            wakeLock.release()
        }
    }
} 