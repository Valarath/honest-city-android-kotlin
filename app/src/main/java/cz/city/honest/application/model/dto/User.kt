package cz.city.honest.application.model.dto

data class User(
    val id: String,
    val username: String,
    val score: Int,
    var logged: Boolean = false,
    val loginData: LoginData
) : HonestCitySerializable

interface LoginData{
    fun getClassName():String = javaClass.simpleName
    fun userId():String
}

enum class LoginProvider{
    FACEBOOK
}