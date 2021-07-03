package cz.city.honest.application.model.dto

import java.io.Serializable

abstract class Vote(open val suggestion: Suggestion, open val userId: String) : HonestCitySerializable

data class VoteForExchangePointDelete(
    override val suggestion: ClosedExchangePointSuggestion,
    override val userId: String
) : Vote(suggestion, userId)

data class VoteForExchangePointRateChange(
    override val suggestion: ExchangeRateSuggestion,
    override val userId: String
) : Vote(suggestion, userId)

data class VoteForNewExchangePoint(
    override val suggestion: NewExchangePointSuggestion,
    override val userId: String
) : Vote(suggestion, userId)