package com.example.lunchmate.model

import com.example.lunchmatelocal.Account
import com.example.lunchmatelocal.Slot

class Notification (private var title: String,
                    private var slot: Slot,
                    private var lunchMate: Account) {

    fun getTitle(): String {
        return title
    }
    fun setTitle(title: String) {
        this.title = title
    }

    fun getContent(): String {
        if (title == "Новое приглашение")
            return lunchMate.getName()+" позвал Вас на ланч"
        else if (title == "Согласие")
            return lunchMate.getName()+" согласился пойти на ланч"
        else if (title == "Отказ")
            return lunchMate.getName()+" отказался идти на ланч"
        else if (title == "Напоминание")
            return lunchMate.getName()+" ждет Вас на ланче через 15 минут"
        return ""
    }

    fun getSlot(): Slot {
        return slot
    }
    fun setSlot(slot: Slot) {
        this.slot = slot
    }

    fun getLunchMate(): Account {
        return lunchMate
    }
    fun setLunchMate(lunchMate: Account) {
        this.lunchMate = lunchMate
    }

    fun getIsSwipeable(): Boolean {
        return title != "Новое приглашение"
    }
}