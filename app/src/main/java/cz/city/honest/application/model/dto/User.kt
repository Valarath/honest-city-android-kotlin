package cz.city.honest.mobile.model.dto

import java.io.Serializable

data class User(val id: String, val username: String, val score: Int) : Serializable