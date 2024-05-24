package com.example.lunchmate.presentation.schedule.ui

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class ScheduleCalendar {
    private val weekdaysNames =
        arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    var calendar: Calendar = Calendar.getInstance()
    var weekNum = calendar.get(Calendar.WEEK_OF_YEAR)
    var dayNum = calendar.get(Calendar.DAY_OF_WEEK)

    fun setCurrentDay(day_num: Int) {
        this.dayNum = day_num
        calendar.set(Calendar.WEEK_OF_YEAR, weekNum)
        calendar.set(Calendar.DAY_OF_WEEK, day_num)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateStr(): String {
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK) - 1] + SimpleDateFormat(", dd MMM yyyyг.").format(
            calendar.time
        )
    }

    fun isCurrentWeek(): Boolean {
        return weekNum <= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
    }

    fun setToday() {
        calendar = Calendar.getInstance()
        weekNum = calendar.get(Calendar.WEEK_OF_YEAR)
        dayNum = calendar.get(Calendar.DAY_OF_WEEK)
    }

}