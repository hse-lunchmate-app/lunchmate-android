package com.example.lunchmate.presentation.profile.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemAvailableSlotBinding
import com.example.lunchmatelocal.Slot


class AvailableSlotsAdapter(
    private val slotsList: ArrayList<Slot>,
    val inviteForLunch: (Int) -> Unit
) :
    RecyclerView.Adapter<AvailableSlotsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAvailableSlotBinding.bind(itemView)
        val parent: CardView
        val time: TextView

        init {
            parent = binding.itemParent
            time = binding.itemTime
        }

        @SuppressLint("SetTextI18n")
        fun bind(model: Slot) {
            time.text =
                getFormatedTime(model.data.startTime) + " - " + getFormatedTime(model.data.endTime)

            parent.setOnClickListener {
                inviteForLunch(model.data.id)
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

    fun getFormatedTime(time: String): String {
        return time.substring(0, 5)
    }
}