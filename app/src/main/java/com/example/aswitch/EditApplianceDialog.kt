package com.example.aswitch

import android.app.TimePickerDialog
import android.content.Context
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import java.text.SimpleDateFormat
import java.util.*

class EditApplianceDialog(
    context: Context,
    private val appliance: Appliance,
    private val onApplianceEdited: (Appliance) -> Unit
) : Dialog(context) {

    private val timeFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
    private var startTime: String = ""
    private var endTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_appliance)

        val etApplianceName = findViewById<EditText>(R.id.etApplianceName)
        val btnStartTime = findViewById<Button>(R.id.btnStartTime)
        val btnEndTime = findViewById<Button>(R.id.btnEndTime)
        val switchPower = findViewById<Switch>(R.id.switchPower)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Set current values
        etApplianceName.setText(appliance.name)
        startTime = appliance.startTime
        endTime = appliance.endTime
        switchPower.isChecked = appliance.isOn

        updateTimeButtons()

        btnStartTime.setOnClickListener {
            showTimePicker(true)
        }

        btnEndTime.setOnClickListener {
            showTimePicker(false)
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnSave.setOnClickListener {
            val name = etApplianceName.text.toString()
            if (name.isNotEmpty()) {
                val editedAppliance = Appliance(
                    name,
                    appliance.type,
                    startTime,
                    endTime,
                    switchPower.isChecked
                )
                onApplianceEdited(editedAppliance)
                dismiss()
            }
        }
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val timeString = timeFormat.format(calendar.time)
                if (isStartTime) {
                    startTime = timeString
                } else {
                    endTime = timeString
                }
                updateTimeButtons()
            },
            currentHour,
            currentMinute,
            false
        ).show()
    }

    private fun updateTimeButtons() {
        val btnStartTime = findViewById<Button>(R.id.btnStartTime)
        val btnEndTime = findViewById<Button>(R.id.btnEndTime)

        btnStartTime.text = if (startTime.isNotEmpty()) startTime else "Select Time"
        btnEndTime.text = if (endTime.isNotEmpty()) endTime else "Select Time"
    }
} 