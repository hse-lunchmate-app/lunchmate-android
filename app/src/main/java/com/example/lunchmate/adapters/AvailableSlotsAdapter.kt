package com.example.lunchmatelocal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemAvailableSlotBinding


class AvailableSlotsAdapter(slotsList: ArrayList<Slot>) :
    RecyclerView.Adapter<AvailableSlotsAdapter.ViewHolder>() {
    private val slotsList: ArrayList<Slot>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAvailableSlotBinding.bind(itemView)
        val parent: CardView
        val time: TextView
        init {
            parent = binding.itemParent
            time = binding.itemTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableSlotsAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_available_slot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableSlotsAdapter.ViewHolder, position: Int) {
        val model: Slot = slotsList[position]
        holder.time.setText(model.getStart()+" - "+model.getFinish())

        holder.parent.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return slotsList.size
    }

    init {
        this.slotsList = slotsList
    }
}