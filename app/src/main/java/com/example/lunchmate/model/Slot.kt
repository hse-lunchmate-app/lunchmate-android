package com.example.lunchmatelocal

class Slot(private var start: String,
           private var finish: String) {

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
}