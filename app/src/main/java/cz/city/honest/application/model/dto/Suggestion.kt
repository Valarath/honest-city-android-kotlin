package cz.city.honest.application.model.dto

abstract class Suggestion(
    open val id: String,
    open val state: State,
    open val votes: Int
) : HonestCitySerializable {
    fun increaseVotes() = votes.apply { this + 1 }
    abstract fun toVote(userId:String): Vote
}

enum class State : HonestCitySerializable {
    ACCEPTED, DECLINED, IN_PROGRESS
}

data class NewExchangePointSuggestion(
    override val id: String,
    override val state: State,
    override val votes: Int,
    val position: Position
) : Suggestion(id, state, votes) {
    override fun toVote(userId: String): Vote = VoteForNewExchangePoint(this,userId)
}

class ExchangeRateSuggestion(
    override val id: String,
    override val state: State,
    override val votes: Int,
    val watchedSubjectId: String,
    val suggestedExchangeRate: ExchangeRate
) : Suggestion(id, state, votes) {
    override fun toVote(userId: String): Vote = VoteForExchangePointRateChange(this,userId)
}

class ClosedExchangePointSuggestion(
    override val id: String,
    override val state: State,
    override val votes: Int,
    val watchedSubjectId: String
) : Suggestion(id, state, votes) {
    override fun toVote(userId: String): Vote = VoteForExchangePointDelete(this,userId)
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
