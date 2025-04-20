package com.example.aswitch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView

class ApplianceAdapter(
    private val appliances: List<Appliance>,
    private val onEditClick: (Appliance) -> Unit,
    private val onApplianceClick: (Appliance) -> Unit
) : RecyclerView.Adapter<ApplianceAdapter.ApplianceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appliance, parent, false)
        return ApplianceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplianceViewHolder, position: Int) {
        holder.bind(appliances[position])
    }

    override fun getItemCount(): Int = appliances.size

    inner class ApplianceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val container: ConstraintLayout = itemView.findViewById(R.id.container)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val switchPower: Switch = itemView.findViewById(R.id.switchPower)
        private val tvStartTime: TextView = itemView.findViewById(R.id.tvStartTime)
        private val tvEndTime: TextView = itemView.findViewById(R.id.tvEndTime)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val tvStartLabel: TextView = itemView.findViewById(R.id.tvStartLabel)
        private val tvEndLabel: TextView = itemView.findViewById(R.id.tvEndLabel)
        private val llTimeInfo: View = itemView.findViewById(R.id.llTimeInfo)
        private val verticalCenter: View = itemView.findViewById(R.id.verticalCenter)

        fun bind(appliance: Appliance) {
            tvName.text = appliance.name
            switchPower.isChecked = appliance.isOn

            // Show/hide time information and adjust name position
            if (appliance.startTime.isNotEmpty() && appliance.endTime.isNotEmpty()) {
                tvStartTime.text = appliance.startTime
                tvEndTime.text = appliance.endTime
                llTimeInfo.visibility = View.VISIBLE
                tvStartTime.visibility = View.VISIBLE
                tvEndTime.visibility = View.VISIBLE
                tvStartLabel.visibility = View.VISIBLE
                tvEndLabel.visibility = View.VISIBLE
                
                // Position name at top
                val constraintSet = ConstraintSet()
                constraintSet.clone(container)
                constraintSet.connect(R.id.tvName, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.clear(R.id.tvName, ConstraintSet.BOTTOM)
                constraintSet.applyTo(container)
            } else {
                llTimeInfo.visibility = View.GONE
                tvStartTime.visibility = View.GONE
                tvEndTime.visibility = View.GONE
                tvStartLabel.visibility = View.GONE
                tvEndLabel.visibility = View.GONE
                
                // Center name vertically
                val constraintSet = ConstraintSet()
                constraintSet.clone(container)
                constraintSet.connect(R.id.tvName, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect(R.id.tvName, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                constraintSet.applyTo(container)
            }

            // Set click listeners
            switchPower.setOnCheckedChangeListener { _, isChecked ->
                appliance.isOn = isChecked
            }

            btnEdit.setOnClickListener {
                onEditClick(appliance)
            }

            itemView.setOnClickListener {
                onApplianceClick(appliance)
            }
        }
    }
} 