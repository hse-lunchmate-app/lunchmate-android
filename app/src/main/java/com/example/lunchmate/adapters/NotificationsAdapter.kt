package com.example.lunchmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemNotificationBinding
import com.example.lunchmate.databinding.ItemSlotBinding
import com.example.lunchmate.model.Notification
import com.example.lunchmatelocal.Slot

class NotificationsAdapter(val onProfileClick: (Int) -> Unit, val denyRequest: (Int) -> Unit,
                           notificationsList: ArrayList<Notification>) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    private val notificationsList: ArrayList<Notification>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemNotificationBinding.bind(itemView)
        val parent: RelativeLayout
        val photo: ImageView
        val title: TextView
        val content: TextView
        val time: TextView
        val btnsPart: LinearLayout
        val denyBtn: AppCompatButton
        val acceptBtn: AppCompatButton
        init {
            parent = binding.parent
            photo = binding.itemPhoto
            title = binding.itemTitle
            content = binding.itemContent
            time = binding.itemDate
            btnsPart = binding.btnsPart
            denyBtn = binding.denyBtn
            acceptBtn = binding.acceptBtn
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationsAdapter.ViewHolder, position: Int) {
        val model: Notification = notificationsList[position]
        holder.title.setText(model.getTitle())
        holder.content.setText(model.getContent())
        holder.time.setText(model.getSlot().getStart()+" - "+model.getSlot().getFinish())

        if (model.getTitle() == "Новое приглашение"){
            holder.btnsPart.visibility = View.VISIBLE
            holder.denyBtn.setOnClickListener {
                denyRequest(position)
            }
            holder.acceptBtn.setOnClickListener {

            }
        }

        holder.parent.setOnClickListener{
            onProfileClick(position)
        }
    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    init {
        this.notificationsList = notificationsList
    }
}