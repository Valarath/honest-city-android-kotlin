package cz.city.honest.dto

abstract class Vote(
    open val suggestion: Suggestion,
    open val userId: String,
    open var processed: Boolean
) : HonestCitySerializable

data class VoteForExchangePointDelete(
    override val suggestion: ClosedExchangePointSuggestion,
    override val userId: String,
    override var processed: Boolean
) : Vote(suggestion, userId, processed)

data class VoteForExchangePointRateChange(
    override val suggestion: ExchangeRateSuggestion,
    override val userId: String,
    override var processed: Boolean
) : Vote(suggestion, userId, processed)

data class VoteForNewExchangePoint(
    override val suggestion: NewExchangePointSuggestion,
    override val userId: String,
    override var processed: Boolean
) : Vote(suggestion, userId, processed)