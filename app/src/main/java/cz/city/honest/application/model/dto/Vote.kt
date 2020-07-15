package cz.city.honest.mobile.model.dto

import cz.city.honest.application.model.dto.Suggestion
import java.io.Serializable

abstract class Vote(open val suggestion: Suggestion) : Serializable

data class VoteForExchangePointDelete(override val suggestion: Suggestion) : Vote(suggestion)
data class VoteForExchangePointRateChange(override val suggestion: Suggestion) : Vote(suggestion)
data class VoteForNewExchangePoint(override val suggestion: Suggestion) : Vote(suggestion)