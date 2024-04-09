package com.example.lunchmate.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.MainActivity
import com.example.lunchmate.R
import com.example.lunchmate.adapters.NotificationsAdapter
import com.example.lunchmate.databinding.FragmentNotificationsBinding
import com.example.lunchmate.model.Notification
import com.example.lunchmatelocal.AvailableSlotsAdapter
import com.example.lunchmatelocal.Slot
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationsFragment: Fragment(R.layout.fragment_notifications) {
    val weekdaysNames = arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var notificationsList: ArrayList<Notification>
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationsBinding.bind(view)
        val activity = activity as MainActivity
        activity.badge_counter = 0
        activity.updateBadge()

        notificationsList = ArrayList<Notification>()
        notificationsList.add(Notification("Новое приглашение", Slot("1 марта", "11:00", "12:00"), activity.currentUser))
        notificationsList.add(Notification("Отказ", Slot("1 марта", "11:00", "12:00"), activity.currentUser))
        notificationsList.add(Notification("Согласие", Slot("1 марта", "11:00", "12:00"), activity.currentUser))
        notificationsList.add(Notification("Напоминание", Slot("1 марта", "11:00", "12:00"), activity.currentUser))

        setUpRV(notificationsList)
    }

    fun onProfileClick(position: Int) {
        var day_num = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val activity = activity as MainActivity
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_profile, null)

        val profileName = view.findViewById<TextView>(R.id.profileName)
        profileName.text = notificationsList[position].getLunchMate().getName()

        val profileNickname = view.findViewById<TextView>(R.id.profileNickname)
        profileNickname.text = notificationsList[position].getLunchMate().getLogin()

        val profileOffice = view.findViewById<TextView>(R.id.office)
        profileOffice.text = activity.offices[notificationsList[position].getLunchMate().getOffice()]

        val profileTaste = view.findViewById<TextView>(R.id.taste)
        profileTaste.text = notificationsList[position].getLunchMate().getTaste()

        val profileInfo = view.findViewById<TextView>(R.id.infoText)
        profileInfo.text = notificationsList[position].getLunchMate().getInfo()

        val tgButton = view.findViewById<MaterialButton>(R.id.tgButton)
        if (notificationsList[position].getLunchMate().getTg() != "" && notificationsList[position].getLunchMate().getTg() != "Без телеграма") {
            tgButton.visibility = View.VISIBLE
            tgButton.setOnClickListener {
                val tgIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/" + notificationsList[position].getLunchMate().getTg())
                )
                startActivity(tgIntent)
            }
        }

        val date = view.findViewById<TextView>(R.id.date)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, day_num)
        date.text = getDateStr(calendar)

        val leftButton = view.findViewById<ImageButton>(R.id.leftButton)
        val rightButton = view.findViewById<ImageButton>(R.id.rightButton)
        rightButton.setOnClickListener{
            calendar.set(Calendar.DAY_OF_YEAR, ++day_num)
            date.text = getDateStr(calendar)
            if (day_num > Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                leftButton.setColorFilter(resources.getColor(R.color.blue_700))
                leftButton.isClickable = true
            }
        }
        leftButton.setOnClickListener{
            calendar.set(Calendar.DAY_OF_YEAR, --day_num)
            date.text = getDateStr(calendar)
            if (day_num <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                leftButton.isClickable = false
            }
        }

        val availableSlots = view.findViewById<RecyclerView>(R.id.availableSlots)
        val slotsList = ArrayList<Slot>()
        slotsList.add(Slot("1 марта", "11:00", "12:00"))
        slotsList.add(Slot("1 марта", "14:00", "15:00"))
        slotsList.add(Slot("1 марта", "14:00", "15:00"))
        slotsList.add(Slot("1 марта", "14:00", "15:00"))
        slotsList.add(Slot("1 марта", "14:00", "15:00"))
        val slotsAdapter = AvailableSlotsAdapter(slotsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        availableSlots.layoutManager = linearLayoutManager
        availableSlots.adapter = slotsAdapter

        val btn = view.findViewById<ImageButton>(R.id.leftButton)
        btn.isClickable = true

        dialog.setContentView(view)
        dialog.show()
    }

    private fun denyRequest(position: Int){
        notificationsList.removeAt(position)
        notificationsAdapter.notifyItemRemoved(position)
    }

    private fun setUpRV(notificationsList: ArrayList<Notification>){
        notificationsAdapter = NotificationsAdapter(::onProfileClick, ::denyRequest, notificationsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = notificationsAdapter
        binding.recyclerView.itemAnimator = SlideInLeftAnimator()

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val notification: Notification =
                    notificationsList.get(position)
                notificationsList.removeAt(position)
                notificationsAdapter.notifyItemRemoved(position)
                val snackBar = Snackbar.make(requireView(), "Уведомление удалено", Snackbar.LENGTH_LONG)
                    .setTextColor(ContextCompat.getColor(
                        requireContext(), R.color.white
                    ))
                    .setActionTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.yellow_600
                        ))
                    .setAction(
                        "Отменить",
                        View.OnClickListener {
                            notificationsList.add(position, notification)
                            notificationsAdapter.notifyItemInserted(position)
                        }).show()
            }
        }).attachToRecyclerView(binding.recyclerView)

        binding.clearBtn.setOnClickListener {
            var i = 0;
            while (i < notificationsList.size){
                if (notificationsList[i].getTitle() != "Новое приглашение"){
                    notificationsList.removeAt(i)
                    notificationsAdapter.notifyItemRemoved(i)
                    i--
                }
                i++
            }
        }
    }

    private fun getDateStr(calendar: Calendar): String{
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK)-1]+ SimpleDateFormat(", dd.MM").format(calendar.time)
    }
}