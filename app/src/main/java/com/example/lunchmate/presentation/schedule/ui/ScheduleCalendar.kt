package com.example.lunchmate.presentation.schedule.ui

import java.text.SimpleDateFormat
import java.util.*

class ScheduleCalendar {
    val weekdaysNames = arrayOf("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    val monthNames = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    val calendar: Calendar = Calendar.getInstance()
    var week_num = calendar.get(Calendar.WEEK_OF_YEAR)
    var day_num = calendar.get(Calendar.DAY_OF_WEEK)

    fun setCurrentDay(day_num: Int){
        this.day_num = day_num
        calendar.set(Calendar.WEEK_OF_YEAR, week_num)
        calendar.set(Calendar.DAY_OF_WEEK, day_num)
    }

    fun getDateStr(): String{
        return weekdaysNames[calendar.get(Calendar.DAY_OF_WEEK)-1]+ SimpleDateFormat(", dd MMM yyyyг.").format(calendar.time)
    }

    fun isCurrentWeek(): Boolean{
        return week_num <= Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
    }
}