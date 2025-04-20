package com.example.aswitch

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvAppliances = findViewById(R.id.rvAppliances)
        fabAddAppliance = findViewById(R.id.fabAddAppliance)

        setupRecyclerView()
        setupClickListeners()
        loadSampleAppliances()
    }

    private fun setupRecyclerView() {
        applianceAdapter = ApplianceAdapter(
            appliances = appliances,
            onEditClick = { appliance ->
                showEditApplianceDialog(appliance)
            },
            onApplianceClick = { appliance ->
                // Toggle the switch state when clicking on the appliance item
                appliance.isOn = !appliance.isOn
                applianceAdapter.notifyDataSetChanged()
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
        }.show()
    }

    private fun showEditDialog(appliance: Appliance) {
        EditApplianceDialog(this, appliance) { editedAppliance ->
            val index = appliances.indexOfFirst { it.name == appliance.name }
            if (index != -1) {
                appliances[index] = editedAppliance
                applianceAdapter.notifyItemChanged(index)
            }
        }.show()
    }

    private fun loadSampleAppliances() {
        appliances.addAll(listOf(
            Appliance("Bedroom Light", ApplianceType.LIGHT, "08:00", "22:00", true),
            Appliance("Living Room Fan", ApplianceType.FAN, "10:00", "18:00", false),
            Appliance("Kitchen Light", ApplianceType.LIGHT, "", "", false),
            Appliance("TV", ApplianceType.TV, "", "", false)
        ))
        applianceAdapter.notifyDataSetChanged()
    }
}