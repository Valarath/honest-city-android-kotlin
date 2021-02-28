package cz.city.honest.application.model.dto

import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.Position
import java.io.Serializable

abstract class Suggestion(
    open val id: Long,
    open val state: State,
    open val votes: Int,
    open val voted:Boolean = false
) : Serializable

enum class State : Serializable {
    ACCEPTED, DECLINED, IN_PROGRESS
}

data class NewExchangePointSuggestion(
    override val id: Long,
    override val state: State,
    override val votes: Int,
    override val voted:Boolean,
    val position: Position,
    val suggestionId: Long
) : Suggestion(suggestionId, state, votes,voted)

class ExchangeRateSuggestion(
    override val id: Long,
    override val state: State,
    override val votes: Int,
    override val voted:Boolean,
    val watchedSubjectId: Long,
    val suggestedExchangeRate: ExchangeRate,
    val suggestionId:Long
) : Suggestion(suggestionId, state, votes,voted)

class ClosedExchangePointSuggestion(
    override val id: Long,
    override val state: State,
    override val votes: Int,
    override val voted:Boolean,
    val watchedSubjectId: Long,
    val suggestionId:Long
) : Suggestion(suggestionId, state, votes,voted)
