package cz.city.honest.dto

import java.time.Instant

sealed class Suggestion(
    open val id: String,
    open val state: State,
    open val subjectId: String?,
    open var votes: Int,
    open val createdAt: Instant
) : HonestCitySerializable  {
    fun increaseVotes() = votes++
    abstract fun toVote(userId: String, processed: Boolean): Vote
}

enum class State : HonestCitySerializable {
    ACCEPTED, DECLINED, IN_PROGRESS
}

data class NewExchangePointSuggestion(
    override val id: String,
    override val state: State,
    override val subjectId: String?,
    override var votes: Int,
    override val createdAt: Instant,
    val position: Position,
    val image: String
) : Suggestion(id, state, subjectId, votes, createdAt) {
    override fun toVote(userId: String, processed: Boolean): Vote =
        Vote(this, userId, processed)
}

class ExchangeRateSuggestion(
    override val id: String,
    override val state: State,
    override val subjectId: String,
    override var votes: Int,
    override val createdAt: Instant,
    val suggestedExchangeRate: ExchangeRate
) : Suggestion(id, state, subjectId, votes, createdAt) {
    override fun toVote(userId: String, processed: Boolean): Vote =
        Vote(this, userId, processed)
}

class ClosedExchangePointSuggestion(
    override val id: String,
    override val state: State,
    override val subjectId: String,
    override var votes: Int,
    override val createdAt: Instant
) : Suggestion(id, state, subjectId, votes, createdAt) {
    override fun toVote(userId: String, processed: Boolean): Vote =
        Vote(this, userId, processed)
}

data class UserSuggestion(
    val user: User,
    val suggestion: Suggestion,
    val metadata: UserSuggestionMetadata
)

data class UserSuggestionMetadata(val processed: Boolean, val markAs: UserSuggestionStateMarking)

enum class UserSuggestionStateMarking {
    NEW, DELETE
}
