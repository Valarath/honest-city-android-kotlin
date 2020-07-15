package cz.city.honest.mobile.model.dto

import java.io.Serializable

data class User(val id: Long, val username: String, val score: Int) : Serializable