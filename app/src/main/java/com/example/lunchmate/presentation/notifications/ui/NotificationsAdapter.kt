package com.example.lunchmate.presentation.notifications.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemNotificationBinding
import com.example.lunchmate.domain.model.Notification

class NotificationsAdapter(
    val onProfileClick: (Int) -> Unit,
    val denyRequest: (Int) -> Unit,
    private val notificationsList: ArrayList<Notification>
) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemNotificationBinding.bind(itemView)
        val parent: LinearLayout
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

        fun bind(model: Notification) {
            title.text = model.getTitle()
            content.text = model.getContent()
            time.text = model.getTime()

            if (model.getTitle() == "Новое приглашение") {
                btnsPart.visibility = View.VISIBLE
                denyBtn.setOnClickListener {
                    denyRequest(position)
                }
                acceptBtn.setOnClickListener {

                }
            }

            parent.setOnClickListener {
                onProfileClick(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Notification = notificationsList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }
}