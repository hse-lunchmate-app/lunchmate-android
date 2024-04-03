package com.example.lunchmatelocal

class Account (private var name: String = "Без имени",
               private var office: Int = 0,
               private var taste: String = "Без предпочтений",
               private var info: String = "Без информации",
               private var tg: String = "Без телеграма",
               private var login: String,
               private var password: String,
               private var photo: Int) {



    fun getName(): String {
        return name
    }
    fun setName(name: String = "Без имени") {
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
        this.taste = taste
    }

    fun getInfo(): String {
        return info
    }
    fun setInfo(info: String = "Без информации") {
        this.info = info
    }

    fun getTg(): String {
        return tg
    }
    fun setTg(tg: String = "Без телеграма") {
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