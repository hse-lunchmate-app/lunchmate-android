package com.example.lunchmate.presentation.schedule.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemSlotBinding
import com.example.lunchmatelocal.Slot

class SlotsAdapter(
    val openFreeSlot: (Slot) -> Unit,
    val openReservedSlot: (Slot) -> Unit,
    private val slotsList: ArrayList<Slot>
) :
    RecyclerView.Adapter<SlotsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSlotBinding.bind(itemView)
        val parent: CardView
        val time: TextView
        val repeatingIndicator: ImageView
        val lunchMate: TextView

        init {
            parent = binding.itemParent
            time = binding.timeLimits
            repeatingIndicator = binding.repeatingIndicator
            lunchMate = binding.lunchMate
        }

        fun bind(model: Slot) {
            time.text = getFormatedTime(model.data.startTime) + " - " + getFormatedTime(model.data.endTime)
            repeatingIndicator.isVisible = model.data.permanent
            if (model.lunchMate != null) {
                lunchMate.visibility = View.VISIBLE
                lunchMate.text = model.lunchMate!!.name
            } else {
                lunchMate.visibility = View.GONE
            }

            parent.setOnClickListener {
                if (lunchMate.isVisible) {
                    openReservedSlot(model)
                } else {
                    openFreeSlot(model)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_slot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Slot = slotsList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return slotsList.size
    }

    private fun getFormatedTime(time: String): String {
        return time.substring(0, 5)
    }
}