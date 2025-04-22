package com.example.aswitch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var rvAppliances: RecyclerView
    private lateinit var fabAddAppliance: ExtendedFloatingActionButton
    private lateinit var applianceAdapter: ApplianceAdapter
    private val appliances = mutableListOf<Appliance>()
    private lateinit var applianceScheduler: ApplianceScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check login state
        val isLoggedIn = getSharedPreferences("login", MODE_PRIVATE)
            .getBoolean("isLoggedIn", false)
        
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        rvAppliances = findViewById(R.id.rvAppliances)
        fabAddAppliance = findViewById(R.id.fabAddAppliance)
        applianceScheduler = ApplianceScheduler(this)

        setupRecyclerView()
        setupClickListeners()
        loadSampleAppliances()
    }

    private fun setupRecyclerView() {
        applianceAdapter = ApplianceAdapter(
            appliances = appliances,
            onEditClick = { appliance ->
                showEditDialog(appliance)
            }
        )
        rvAppliances.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = applianceAdapter
        }
    }

    private fun setupClickListeners() {
        fabAddAppliance.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        AddApplianceDialog(this) { newAppliance ->
            appliances.add(newAppliance)
            applianceAdapter.notifyItemInserted(appliances.size - 1)
            // Schedule the new appliance if it has times set
            if (newAppliance.startTime.isNotEmpty() && newAppliance.endTime.isNotEmpty()) {
                applianceScheduler.scheduleAppliance(newAppliance)
            }
        }.show()
    }

    private fun showEditDialog(appliance: Appliance) {
        EditApplianceDialog(this, appliance,
            onSave = { editedAppliance ->
                val index = appliances.indexOfFirst { it.name == appliance.name }
                if (index != -1) {
                    // Cancel existing schedule if times are being changed
                    if (appliance.startTime != editedAppliance.startTime || 
                        appliance.endTime != editedAppliance.endTime) {
                        applianceScheduler.cancelSchedule(appliance)
                    }
                    
                    appliances[index] = editedAppliance
                    applianceAdapter.notifyItemChanged(index)
                    
                    // Schedule the edited appliance if it has times set
                    if (editedAppliance.startTime.isNotEmpty() && editedAppliance.endTime.isNotEmpty()) {
                        applianceScheduler.scheduleAppliance(editedAppliance)
                    }
                }
            },
            onDelete = {
                val index = appliances.indexOfFirst { it.name == appliance.name }
                if (index != -1) {
                    // Cancel the schedule before removing the appliance
                    applianceScheduler.cancelSchedule(appliance)
                    appliances.removeAt(index)
                    applianceAdapter.notifyItemRemoved(index)
                }
            }
        ).show()
    }

    private fun loadSampleAppliances() {
        // Try to load from shared preferences first
        val sharedPrefs = getSharedPreferences("appliances", MODE_PRIVATE)
        val appliancesJson = sharedPrefs.getString("appliances_list", null)
        
        if (appliancesJson != null) {
            appliances.addAll(Appliance.fromJsonArray(appliancesJson))
        } else {
            // If no saved data, use sample appliances
            appliances.addAll(listOf(
                Appliance("Relay 1 - Bedroom Light", ApplianceType.LIGHT, "08:00 AM", "10:00 PM", false),
                Appliance("Relay 2 - Living Room Fan", ApplianceType.FAN, "10:00 AM", "06:00 PM", false),
                Appliance("Relay 3 - Kitchen Light", ApplianceType.LIGHT, "", "", false),
                Appliance("Relay 4 - TV", ApplianceType.TV, "", "", false)
            ))
            // Save the sample appliances
            sharedPrefs.edit().putString("appliances_list", Appliance.toJsonArray(appliances)).apply()
        }
        
        applianceAdapter.notifyDataSetChanged()
        
        // Schedule appliances that have times set
        appliances.forEach { appliance ->
            if (appliance.startTime.isNotEmpty() && appliance.endTime.isNotEmpty()) {
                applianceScheduler.scheduleAppliance(appliance)
            }
        }
    }

    fun updateApplianceState(applianceName: String, isOn: Boolean) {
        val index = appliances.indexOfFirst { it.name == applianceName }
        if (index != -1) {
            appliances[index] = appliances[index].copy(isOn = isOn)
            applianceAdapter.updateApplianceState(applianceName, isOn)
            
            // Save the updated state
            val sharedPrefs = getSharedPreferences("appliances", MODE_PRIVATE)
            sharedPrefs.edit().putString("appliances_list", Appliance.toJsonArray(appliances)).apply()
        }
    }
}