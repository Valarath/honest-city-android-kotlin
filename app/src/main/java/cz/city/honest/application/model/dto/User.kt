package cz.city.honest.application.model.dto

import java.io.Serializable

data class User(val id: String, val username: String, val score: Int, val logged:Boolean) : Serializable

enum class LoginProvider{
    FACEBOOK
}