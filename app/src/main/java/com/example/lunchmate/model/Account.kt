package com.example.lunchmatelocal

class Account (private val id: Int,
               private var name: String = "Без имени",
               private var office: Int = 0,
               private var taste: String = "Без предпочтений",
               private var info: String = "Без информации",
               private var tg: String = "Без телеграма",
               private var login: String,
               private var password: String,
               private var photo: Int) {

    fun getId(): Int {
        return id
    }

    fun getName(): String {
        return name
    }
    fun setName(name: String = "Без имени") {
        if (name == "")
            this.name = "Без имени"
        else
            this.name = name
    }

    fun getOffice(): Int {
        return office
    }
    fun setOffice(office: Int) {
        this.office = office
    }

    fun getTaste(): String {
        return taste
    }
    fun setTaste(taste: String = "Без предпочтений") {
        if (taste == "")
            this.taste = "Без предпочтений"
        else
            this.taste = taste
    }

    fun getInfo(): String {
        return info
    }
    fun setInfo(info: String = "Без информации") {
        if (info == "")
            this.info = "Без информации"
        else
            this.info = info
    }

    fun getTg(): String {
        return tg
    }
    fun setTg(tg: String = "Без телеграма") {
        if (tg == "")
            this.tg = "Без телеграма"
        else
            this.tg = tg
    }

    fun getLogin(): String {
        return login
    }
    fun setLogin(login: String) {
        this.login = login
    }

    fun getPassword(): String {
        return password
    }
    fun setPassword(password: String) {
        this.password = password
    }

    fun getPhoto(): Int {
        return photo
    }
    fun setPhoto(photo: Int) {
        this.photo = photo
    }
}