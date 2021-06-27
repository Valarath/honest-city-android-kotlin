package cz.city.honest.application.model.dto

import java.io.Serializable

open class User(
    val id: String,
    val username: String,
    val score: Int,
    var logged: Boolean,
    val loginProvider: LoginProvider
) : Serializable

interface LoginData

class LoginDataUser(id: String, username: String, score: Int, logged: Boolean, loginProvider: LoginProvider, val loginData: LoginData) :
    User(id, username, score, logged, loginProvider)


enum class LoginProvider{
    FACEBOOK
}