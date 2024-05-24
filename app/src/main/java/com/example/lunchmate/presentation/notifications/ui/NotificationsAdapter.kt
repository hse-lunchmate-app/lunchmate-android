package com.example.lunchmate.presentation.notifications.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.R
import com.example.lunchmate.databinding.ItemNotificationBinding
import com.example.lunchmate.domain.model.Lunch
import com.example.lunchmate.domain.model.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationsAdapter(
    private val notificationsList: ArrayList<Lunch>,
    val onProfileClick: (User) -> Unit,
    val declineInvitation: (Lunch) -> Unit,
    val acceptInvitation: (Lunch) -> Unit,
    val revokeInvitation: (Lunch) -> Unit,
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
        val progressBar: ProgressBar
        val revokeBtn: AppCompatButton

        init {
            parent = binding.parent
            photo = binding.itemPhoto
            title = binding.itemTitle
            content = binding.itemContent
            time = binding.itemDate
            btnsPart = binding.btnsPart
            denyBtn = binding.denyBtn
            acceptBtn = binding.acceptBtn
            progressBar = binding.progressBar
            revokeBtn = binding.revokeBtn
        }

        @SuppressLint("SetTextI18n")
        fun bind(model: Lunch) {
            if (!model.accepted && model.invitee.id == "id1") {
                title.text = "Новое приглашение"
                content.text = model.master.name + " хочет пойти на ланч"
                time.text = getTime(model.lunchDate, model.timeslot.startTime, model.timeslot.endTime)
                btnsPart.visibility = View.VISIBLE
                denyBtn.setOnClickListener {
                    declineInvitation(model)
                }
                acceptBtn.setOnClickListener {
                    acceptBtn.text = ""
                    progressBar.visibility = View.VISIBLE
                    acceptInvitation(model)
                }
                parent.setOnClickListener {
                    onProfileClick(model.master)
                }
            }
            else if (!model.accepted && model.master.id == "id1") {
                title.text = "Ожидание ответа"
                content.text = "Вы ждете ответ от пользователя: " + model.invitee.name
                time.text = getTime(model.lunchDate, model.timeslot.startTime, model.timeslot.endTime)
                revokeBtn.visibility = View.VISIBLE
                revokeBtn.setOnClickListener {
                    revokeInvitation(model)
                }
                parent.setOnClickListener {
                    onProfileClick(model.invitee)
                }
            }
            else if (model.accepted && model.master.id == "id1"){
                title.text = "Ваше приглашение приняли"
                content.text = model.invitee.name + " будет ждать Вас на ланче"
                time.text = getTime(model.lunchDate, model.timeslot.startTime, model.timeslot.endTime)
                parent.setOnClickListener {
                    onProfileClick(model.invitee)
                }
            }
            else if (model.accepted && model.invitee.id == "id1"){
                title.text = "Вы приняли приглашение"
                content.text = model.master.name + " будет ждать Вас на ланче"
                time.text = getTime(model.lunchDate, model.timeslot.startTime, model.timeslot.endTime)
                parent.setOnClickListener {
                    onProfileClick(model.master)
                }
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
        val model: Lunch = notificationsList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(date: String, start:String, finish: String): String{
        val OLD_FORMAT = "yyyy-MM-dd"
        val NEW_FORMAT = "dd MMMM"

        val sdf = SimpleDateFormat(OLD_FORMAT)
        val d: Date = sdf.parse(date) as Date
        sdf.applyPattern(NEW_FORMAT)

        return "Дата: " + sdf.format(d) + ", c " + start.substring(0, 5) + " до " + finish.substring(0, 5)
    }
}