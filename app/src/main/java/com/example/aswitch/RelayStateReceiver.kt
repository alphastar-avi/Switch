package com.example.aswitch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RelayStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.aswitch.RELAY_STATE_CHANGED") {
            val applianceName = intent.getStringExtra("appliance_name") ?: return
            val isOn = intent.getBooleanExtra("is_on", false)
            
            // Update the UI through MainActivity
            (context as? MainActivity)?.updateApplianceState(applianceName, isOn)
        }
    }
} 