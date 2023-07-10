package cz.city.honest.dto

open class Vote(
    open val suggestion: Suggestion,
    open val userId: String,
    open var processed: Boolean
) : HonestCitySerializable