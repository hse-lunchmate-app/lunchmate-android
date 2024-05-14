package com.example.lunchmate.themeManager

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.lunchmate.R

class LightTheme : MyAppTheme {

    override fun id(): Int { // set unique iD for each theme
        return 0
    }

    override fun firstActivityBackgroundColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.white)
    }

    override fun firstActivityTextColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.black)
    }

    override fun firstActivityIconColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.blue_700)
    }
}