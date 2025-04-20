package com.example.aswitch

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class ApplianceScheduler(private val context: Context) {
    companion object {
        private const val TAG = "ApplianceScheduler"
        private val timeFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAppliance(appliance: Appliance) {
        if (appliance.startTime.isEmpty() || appliance.endTime.isEmpty()) {
            return
        }

        try {
            // Parse start and end times
            val startTime = timeFormat.parse(appliance.startTime)
            val endTime = timeFormat.parse(appliance.endTime)

            if (startTime != null && endTime != null) {
                // Create calendar instances for today
                val now = Calendar.getInstance()
                val startCalendar = Calendar.getInstance().apply {
                    time = startTime
                    // Set the date to today
                    set(Calendar.YEAR, now.get(Calendar.YEAR))
                    set(Calendar.MONTH, now.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                    
                    // If the time has already passed today, schedule for tomorrow
                    if (timeInMillis <= System.currentTimeMillis()) {
                        Log.d(TAG, "Start time has passed, scheduling for tomorrow")
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                val endCalendar = Calendar.getInstance().apply {
                    time = endTime
                    // Set the date to today
                    set(Calendar.YEAR, now.get(Calendar.YEAR))
                    set(Calendar.MONTH, now.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                    
                    // If the time has already passed today, schedule for tomorrow
                    if (timeInMillis <= System.currentTimeMillis()) {
                        Log.d(TAG, "End time has passed, scheduling for tomorrow")
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                    
                    // Ensure end time is after start time
                    if (timeInMillis <= startCalendar.timeInMillis) {
                        Log.d(TAG, "End time is before start time, adjusting end time")
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                // Get relay number from appliance name
                val relayNumber = try {
                    val regex = "Relay\\s+(\\d+)".toRegex()
                    val match = regex.find(appliance.name)
                    if (match != null) {
                        match.groupValues[1].toInt()
                    } else {
                        Log.e(TAG, "Could not extract relay number from name: ${appliance.name}")
                        1
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error extracting relay number: ${e.message}")
                    1
                }

                Log.d(TAG, "Scheduling alarms for ${appliance.name} (relay $relayNumber)")
                Log.d(TAG, "Current time: ${timeFormat.format(now.time)}")
                Log.d(TAG, "Start time: ${timeFormat.format(startCalendar.time)} (${startCalendar.timeInMillis})")
                Log.d(TAG, "End time: ${timeFormat.format(endCalendar.time)} (${endCalendar.timeInMillis})")

                // Create intents for start and end actions
                val startIntent = Intent(context, ApplianceAlarmReceiver::class.java).apply {
                    putExtra("appliance_name", appliance.name)
                    putExtra("action", "start")
                    putExtra("relay_number", relayNumber)
                }

                val endIntent = Intent(context, ApplianceAlarmReceiver::class.java).apply {
                    putExtra("appliance_name", appliance.name)
                    putExtra("action", "end")
                    putExtra("relay_number", relayNumber)
                }

                // Create pending intents with unique request codes
                val startRequestCode = appliance.name.hashCode()
                val endRequestCode = appliance.name.hashCode() + 1

                val startPendingIntent = PendingIntent.getBroadcast(
                    context,
                    startRequestCode,
                    startIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val endPendingIntent = PendingIntent.getBroadcast(
                    context,
                    endRequestCode,
                    endIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Cancel any existing alarms first
                alarmManager.cancel(startPendingIntent)
                alarmManager.cancel(endPendingIntent)

                // Schedule alarms with exact timing
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    startCalendar.timeInMillis,
                    startPendingIntent
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    endCalendar.timeInMillis,
                    endPendingIntent
                )

                Log.d(TAG, "Alarms scheduled successfully for ${appliance.name}")
                Log.d(TAG, "Time until start: ${(startCalendar.timeInMillis - System.currentTimeMillis()) / 1000} seconds")
                Log.d(TAG, "Time until end: ${(endCalendar.timeInMillis - System.currentTimeMillis()) / 1000} seconds")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling appliance: ${e.message}")
        }
    }

    fun cancelSchedule(appliance: Appliance) {
        val startIntent = Intent(context, ApplianceAlarmReceiver::class.java).apply {
            putExtra("appliance_name", appliance.name)
            putExtra("action", "start")
        }

        val endIntent = Intent(context, ApplianceAlarmReceiver::class.java).apply {
            putExtra("appliance_name", appliance.name)
            putExtra("action", "end")
        }

        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            appliance.name.hashCode(),
            startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val endPendingIntent = PendingIntent.getBroadcast(
            context,
            appliance.name.hashCode() + 1,
            endIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(startPendingIntent)
        alarmManager.cancel(endPendingIntent)
        Log.d(TAG, "Cancelled schedule for ${appliance.name}")
    }
} 