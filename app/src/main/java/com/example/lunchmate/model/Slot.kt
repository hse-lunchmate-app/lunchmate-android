package com.example.lunchmatelocal

class Slot(private var date: String,
           private var start: String,
           private var finish: String,
           private var isRepeating: Boolean = false,
           private var lunchMate: Account? = null) {

    fun getDate(): String {
        return date
    }
    fun setDate(date: String) {
        this.date = date
    }

    fun getStart(): String {
        return start
    }
    fun setStart(start: String) {
        this.start = start
    }

    fun getFinish(): String {
        return finish
    }
    fun setFinish(finish: String) {
        this.finish = finish
    }

    fun getIsRepeating(): Boolean {
        return isRepeating
    }
    fun setIsRepeating(isRepeating: Boolean = false) {
        this.isRepeating = isRepeating
    }

    fun getLunchMate(): Account? {
        return lunchMate
    }
    fun setLunchMate(lunchMate: Account) {
        this.lunchMate = lunchMate
    }
}