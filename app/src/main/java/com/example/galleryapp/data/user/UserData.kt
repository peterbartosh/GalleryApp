package com.example.galleryapp.data.user



object UserData {

    lateinit var token: String
        private set
    var userId: Int = 0
        private set
    var login: String = "Username"
        private set

    fun init(token: String?, userId: Int?, login: String?) =
        if (token == null || userId == null || login == null) {
            false
        }
        else {
            this.token = token
            this.userId = userId
            this.login = login
            true
        }

}