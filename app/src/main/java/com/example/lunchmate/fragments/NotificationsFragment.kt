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
import com.example.lunchmate.databinding.BottomSheetProfileBinding
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
        //notificationsList.add(Notification(0, "Новое приглашение", "1 марта, 13:00 - 14:00", activity.currentUser))
        //notificationsList.add(Notification(0, "Отказ", "1 марта, 13:00 - 14:00", activity.currentUser))
        //notificationsList.add(Notification(0, "Согласие", "1 марта, 13:00 - 14:00", activity.currentUser))
        //notificationsList.add(Notification(0, "Напоминание", "1 марта, 13:00 - 14:00", activity.currentUser))

        setUpRV(notificationsList)
    }

    private fun onProfileClick(position: Int) {
        var day_num = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val activity = activity as MainActivity
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)
        val bottomBinding = BottomSheetProfileBinding.bind(layoutInflater.inflate(R.layout.bottom_sheet_profile, null))

        bottomBinding.profileName.text = notificationsList[position].getLunchMate().getName()

        bottomBinding.profileNickname.text = notificationsList[position].getLunchMate().getLogin()

        //bottomBinding.office.text = activity.offices[notificationsList[position].getLunchMate().getOffice()]

        bottomBinding.taste.text = notificationsList[position].getLunchMate().getTaste()

        bottomBinding.infoText.text = notificationsList[position].getLunchMate().getInfo()

        if (notificationsList[position].getLunchMate().getTg() != "" && notificationsList[position].getLunchMate().getTg() != "Без телеграма") {
            bottomBinding.tgButton.visibility = View.VISIBLE
            bottomBinding.tgButton.setOnClickListener {
                val tgIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/" + notificationsList[position].getLunchMate().getTg())
                )
                startActivity(tgIntent)
            }
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, day_num)
        bottomBinding.date.text = getDateStr(calendar)

        bottomBinding.rightButton.setOnClickListener{
            bottomBinding.leftButton.isEnabled = true
            calendar.set(Calendar.DAY_OF_YEAR, ++day_num)
            bottomBinding.date.text = getDateStr(calendar)
            if (day_num > Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                bottomBinding.leftButton.setColorFilter(resources.getColor(R.color.blue_700))
                bottomBinding.leftButton.isClickable = true
            }
        }
        bottomBinding.leftButton.isEnabled = false
        bottomBinding.leftButton.setOnClickListener{
            calendar.set(Calendar.DAY_OF_YEAR, --day_num)
            bottomBinding.date.text = getDateStr(calendar)
            if (day_num <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                bottomBinding.leftButton.setColorFilter(resources.getColor(R.color.grey_400))
                bottomBinding.leftButton.isClickable = false
            }
        }

        val slotsList = ArrayList<Slot>()
        slotsList.add(Slot(0, "1 марта", "11:00", "12:00"))
        slotsList.add(Slot(1, "1 марта", "14:00", "15:00"))
        slotsList.add(Slot(2, "1 марта", "14:00", "15:00"))
        slotsList.add(Slot(3, "1 марта", "14:00", "15:00"))
        slotsList.add(Slot(4, "1 марта", "14:00", "15:00"))
        val slotsAdapter = AvailableSlotsAdapter(slotsList)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        bottomBinding.availableSlots.layoutManager = linearLayoutManager
        bottomBinding.availableSlots.adapter = slotsAdapter

        dialog.setContentView(bottomBinding.root)
        dialog.show()
    }

    private fun denyRequest(position: Int){
        notificationsList.removeAt(position)
        notificationsAdapter.notifyItemRemoved(position)
        checkEmptyState()
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
                checkEmptyState()

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
                            checkEmptyState()
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
            checkEmptyState()
        }

        checkEmptyState()
    }

    private fun getDateStr(calendar: Calendar): String{
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK)-1]+ SimpleDateFormat(", dd.MM").format(calendar.time)
    }

    private fun checkEmptyState() {
        if (notificationsList.size == 0) {
            binding.emptyIcon.visibility = View.VISIBLE
            binding.emptyTitle.visibility = View.VISIBLE
            binding.emptyContent.visibility = View.VISIBLE
        } else {
            binding.emptyIcon.visibility = View.GONE
            binding.emptyTitle.visibility = View.GONE
            binding.emptyContent.visibility = View.GONE
        }
    }
}