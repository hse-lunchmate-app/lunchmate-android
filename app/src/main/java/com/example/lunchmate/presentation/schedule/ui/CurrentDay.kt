package com.example.lunchmate.presentation.schedule.ui

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

data class CurrentDay(
    var weekday: Int,
    var btn: LinearLayout,
    var text: TextView,
    var dateView: TextView,
    var slotsIndicator: ImageView,
)