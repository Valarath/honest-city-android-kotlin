package cz.city.honest.application.model.dto

import java.io.Serializable

data class User(
    val id: String,
    val username: String,
    val score: Int,
    var logged: Boolean = false,
    val loginData: LoginData
) : Serializable

interface LoginData{
    fun getClassName():String = javaClass.simpleName
    fun userId():String
}

enum class LoginProvider{
    FACEBOOK
}