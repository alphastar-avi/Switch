package com.example.aswitch

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddApplianceDialog(
    context: Context,
    private val onApplianceAdded: (Appliance) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_appliance)

        val etApplianceName = findViewById<EditText>(R.id.etApplianceName)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnAdd.setOnClickListener {
            val name = etApplianceName.text.toString()
            if (name.isNotEmpty()) {
                val appliance = Appliance(name, ApplianceType.LIGHT, "", "", false)
                onApplianceAdded(appliance)
                dismiss()
            }
        }
    }
} 