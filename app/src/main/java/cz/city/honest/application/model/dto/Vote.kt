package cz.city.honest.mobile.model.dto

import cz.city.honest.application.model.dto.ClosedExchangePointSuggestion
import cz.city.honest.application.model.dto.ExchangeRateSuggestion
import cz.city.honest.application.model.dto.NewExchangePointSuggestion
import cz.city.honest.application.model.dto.Suggestion
import java.io.Serializable

abstract class Vote(open val suggestion: Suggestion) : Serializable

data class VoteForExchangePointDelete(override val suggestion: ClosedExchangePointSuggestion) : Vote(suggestion)
data class VoteForExchangePointRateChange(override val suggestion: ExchangeRateSuggestion) : Vote(suggestion)
data class VoteForNewExchangePoint(override val suggestion: NewExchangePointSuggestion) : Vote(suggestion)