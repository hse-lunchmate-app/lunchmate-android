package com.example.lunchmate.presentation.availableSlots

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemAvailableSlotBinding
import com.example.lunchmatelocal.Slot


class AvailableSlotsAdapter(private val slotsList: ArrayList<Slot>) :
    RecyclerView.Adapter<AvailableSlotsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAvailableSlotBinding.bind(itemView)
        val parent: CardView
        val time: TextView

        init {
            parent = binding.itemParent
            time = binding.itemTime
        }

        fun bind(model: Slot) {
            time.text = model.getStart() + " - " + model.getFinish()

            parent.setOnClickListener {
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_available_slot, parent, false)
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