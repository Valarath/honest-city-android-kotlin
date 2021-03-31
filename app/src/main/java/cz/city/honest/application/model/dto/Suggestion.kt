package cz.city.honest.application.model.dto

import cz.city.honest.mobile.model.dto.ExchangeRate
import cz.city.honest.mobile.model.dto.Position
import cz.city.honest.mobile.model.dto.User
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
    val position: Position,
    val suggestionId: Long
) : Suggestion(suggestionId, state, votes)

class ExchangeRateSuggestion(
    override val id: Long,
    override val state: State,
    override val votes: Int,
    val watchedSubjectId: Long,
    val suggestedExchangeRate: ExchangeRate,
    val suggestionId:Long
) : Suggestion(suggestionId, state, votes)

class ClosedExchangePointSuggestion(
    override val id: Long,
    override val state: State,
    override val votes: Int,
    val watchedSubjectId: Long,
    val suggestionId:Long
) : Suggestion(suggestionId, state, votes)

data class UserSuggestion(val user: User, val suggestion: Suggestion, val metadata: UserSuggestionMetadata )

data class UserSuggestionMetadata(val processed: Boolean, val markAs: UserSuggestionStateMarking)

enum class UserSuggestionStateMarking{
    NEW,DELETE
}
