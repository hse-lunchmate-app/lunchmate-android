package com.example.lunchmate.presentation.profile.ui

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class ProfileCalendar(val updateScheduleData: () -> Unit) {
    private val weekdaysNames =
        arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    private val calendar: Calendar = Calendar.getInstance()
    private var dayNum = calendar.get(Calendar.DAY_OF_YEAR)

    @SuppressLint("SimpleDateFormat")
    fun getDateStr(): String {
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK) - 1] + SimpleDateFormat(", dd MMM yyyyг.").format(
            calendar.time
        )
    }

    fun isToday(): Boolean {
        return dayNum <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
    }

    fun increase() {
        calendar.set(Calendar.DAY_OF_YEAR, ++dayNum)
        updateScheduleData()
    }

    fun decrease() {
        calendar.set(Calendar.DAY_OF_YEAR, --dayNum)
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