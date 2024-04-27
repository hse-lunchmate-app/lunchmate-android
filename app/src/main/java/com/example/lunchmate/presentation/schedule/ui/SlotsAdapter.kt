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
    val openFreeSlot: (Int) -> Unit,
    val openReservedSlot: (Int) -> Unit,
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
            time.text = model.getStart() + " - " + model.getFinish()
            repeatingIndicator.isVisible = model.getIsRepeating()
            if (model.getLunchMate() != null) {
                lunchMate.visibility = View.VISIBLE
                lunchMate.text = model.getLunchMate()!!.getName()
            } else {
                lunchMate.visibility = View.GONE
            }

            parent.setOnClickListener {
                if (lunchMate.isVisible) {
                    openReservedSlot(position)
                } else {
                    openFreeSlot(position)
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
}