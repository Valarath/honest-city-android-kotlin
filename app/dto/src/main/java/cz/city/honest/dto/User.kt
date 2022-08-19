package cz.city.honest.dto

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
data class FacebookLoginData(
    val accessToken: String = "",
    val facebookUserId: String,
    val userId:String
) : LoginData {
    override fun userId(): String = userId
}

enum class LoginProvider{
    FACEBOOK
}