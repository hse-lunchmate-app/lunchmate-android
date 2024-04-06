package com.example.lunchmate.adapters

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

class SlotsAdapter(val openFreeSlot: (Int) -> Unit, val openReservedSlot: (Int) -> Unit, slotsList: ArrayList<Slot>) :
    RecyclerView.Adapter<SlotsAdapter.ViewHolder>() {
    private val slotsList: ArrayList<Slot>

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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotsAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_slot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotsAdapter.ViewHolder, position: Int) {
        val model: Slot = slotsList[position]
        holder.time.setText(model.getStart()+" - "+model.getFinish())
        holder.repeatingIndicator.isVisible = model.getIsRepeating()
        if (model.getLunchMate() != null){
            holder.lunchMate.visibility = View.VISIBLE
            holder.lunchMate.text = model.getLunchMate()!!.getName()
        }

        holder.parent.setOnClickListener {
            if (holder.lunchMate.isVisible){
                openReservedSlot(position)
            }
            else{
                openFreeSlot(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return slotsList.size
    }

    init {
        this.slotsList = slotsList
    }
}