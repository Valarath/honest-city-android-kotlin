package cz.city.honest.application.model.dto

import java.io.Serializable

interface HonestCitySerializable : Serializable{

    fun getClassName():String = javaClass.simpleName
}