package cz.city.honest.dto

import java.io.Serializable

interface HonestCitySerializable : Serializable{

    fun getClassName():String = javaClass.simpleName
}