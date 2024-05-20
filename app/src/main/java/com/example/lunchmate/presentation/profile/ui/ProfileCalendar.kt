package com.example.lunchmate.presentation.profile.ui

import java.text.SimpleDateFormat
import java.util.*

class ProfileCalendar(val updateScheduleData: () -> Unit) {
    val weekdaysNames = arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    val calendar: Calendar = Calendar.getInstance()
    var day_num = calendar.get(Calendar.DAY_OF_YEAR)

    fun getDateStr(): String{
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK)-1]+ SimpleDateFormat(", dd MMM yyyyг.").format(calendar.time)
    }

    fun isToday(): Boolean{
        return day_num <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
    }

    fun increase() {
        calendar.set(Calendar.DAY_OF_YEAR, ++day_num)
        updateScheduleData()
    }

    fun decrease() {
        calendar.set(Calendar.DAY_OF_YEAR, --day_num)
        updateScheduleData()
    }

    fun getCurrentWeekday(): Int {
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                return 1
            }
            Calendar.TUESDAY -> {
                return 2
            }
            Calendar.WEDNESDAY -> {
                return 3
            }
            Calendar.THURSDAY -> {
                return 4
            }
            Calendar.FRIDAY -> {
                return 5
            }
        }
        return 0
    }

}