package cz.city.honest.mobile.model.dto

import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion
import java.io.Serializable

abstract class Vote(open val suggestion: Suggestion, open val userId: String) : Serializable

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