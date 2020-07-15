package cz.city.honest.application.model.dto

import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.Position
import java.io.Serializable

abstract class Suggestion(
    open val id: Long,
    open val state: State,
    open val votes: Int
) : Serializable

enum class State : Serializable {
    ACCEPTED, DECLINED, IN_PROGRESS
}

data class NewExchangePointSuggestion(
    override val id: Long,
    override val state: State,
    override val votes: Int,
    val position: Position
) : Suggestion(id, state, votes)

class ExchangeRateSuggestion(
    override val id: Long,
    override val state: State,
    override val votes: Int,
    val exchangePointId: Long,
    val suggestedExchangeRate: ExchangeRate
) : Suggestion(id, state, votes)

class ClosedExchangePointSuggestion(
    override val id: Long,
    override val state: State,
    override val votes: Int,
    val exchangePointId: Long
) : Suggestion(id, state, votes)
