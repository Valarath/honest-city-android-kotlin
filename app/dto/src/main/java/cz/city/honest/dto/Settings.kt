package cz.city.honest.dto

data class CurrencySettings(val id: String, val currency:String, val mainCountryCurrency: Boolean):
    HonestCitySerializable