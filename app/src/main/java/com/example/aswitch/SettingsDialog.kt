package com.example.aswitch

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SettingsDialog : DialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private var onIpChangedListener: OnIpChangedListener? = null

    interface OnIpChangedListener {
        fun onIpChanged(newIp: String)
    }

    fun setOnIpChangedListener(listener: OnIpChangedListener) {
        onIpChangedListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_settings, null)
        
        val etIpAddress = view.findViewById<EditText>(R.id.etIpAddress)
        val btnSaveIp = view.findViewById<Button>(R.id.btnSaveIp)
        val btnDeviceData = view.findViewById<Button>(R.id.btnDeviceData)
        
        // Load current IP address
        val currentIp = sharedPreferences.getString("esp32_ip", "http://192.168.1.11") ?: "http://192.168.1.11"
        etIpAddress.setText(currentIp)
        
        btnSaveIp.setOnClickListener {
            val newIp = etIpAddress.text.toString().trim()
            if (newIp.isNotEmpty()) {
                // Save the new IP address
                sharedPreferences.edit().putString("esp32_ip", newIp).apply()
                onIpChangedListener?.onIpChanged(newIp)
                Toast.makeText(context, "IP address saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter a valid IP address", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnDeviceData.setOnClickListener {
            // We'll implement this later when you tell me what to do with device data
            Toast.makeText(context, "Device data functionality coming soon", Toast.LENGTH_SHORT).show()
        }
        
        builder.setView(view)
        return builder.create()
    }
} 