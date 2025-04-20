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
    private val onSave: (Appliance) -> Unit,
    private val onDelete: () -> Unit
) : Dialog(context) {

    private val timeFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
    private var startTime: String = ""
    private var endTime: String = ""
    private lateinit var etApplianceName: EditText
    private lateinit var btnStartTime: Button
    private lateinit var btnEndTime: Button
    private lateinit var switchPower: Switch
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnResetTimer: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_appliance)

        etApplianceName = findViewById(R.id.etApplianceName)
        btnStartTime = findViewById(R.id.btnStartTime)
        btnEndTime = findViewById(R.id.btnEndTime)
        switchPower = findViewById(R.id.switchPower)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        btnResetTimer = findViewById(R.id.btnResetTimer)
        btnDelete = findViewById(R.id.btnDelete)

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

        btnResetTimer.setOnClickListener {
            startTime = ""
            endTime = ""
            updateTimeButtons()
        }

        btnDelete.setOnClickListener {
            onDelete()
            dismiss()
        }

        btnSave.setOnClickListener {
            val name = etApplianceName.text.toString().trim()
            if (name.isEmpty()) {
                etApplianceName.error = "Name is required"
                return@setOnClickListener
            }

            val editedAppliance = Appliance(
                name = name,
                type = appliance.type,
                startTime = startTime,
                endTime = endTime,
                isOn = switchPower.isChecked
            )
            onSave(editedAppliance)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
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
        btnStartTime.text = if (startTime.isNotEmpty()) startTime else "Select Time"
        btnEndTime.text = if (endTime.isNotEmpty()) endTime else "Select Time"
    }
} 