package cz.city.honest.service.vote

import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.User
import cz.city.honest.dto.Vote
import cz.city.honest.service.gateway.external.ExternalVoteGateway
import cz.city.honest.service.gateway.internal.InternalVoteGateway
import cz.city.honest.service.update.PrivateUpdatable
import cz.city.honest.service.user.UserProvider
import io.reactivex.rxjava3.core.Observable

class VoteService(
    private val internalVoteGateway: InternalVoteGateway,
    private val externalVoteGateway: ExternalVoteGateway,
    private val userProvider: UserProvider
) : PrivateUpdatable {

    override fun update(accessToken: String): Observable<Unit> =
        userProvider.provide()
            .flatMap { updateVotes(it, accessToken) }
    //.onErrorComplete()

    fun findBySuggestionId(suggestionId: String) = internalVoteGateway.findBySuggestionId(suggestionId)

    private fun updateVotes(user: User, accessToken: String) =
        internalVoteGateway.updateVotes(user)
            .filter { !it.processed }
            .map { setAsProcessed(it) }
            .toList()
            .toObservable()
            .flatMap { updateVotes(it, user, accessToken) }

    private fun updateVotes(votes: List<Vote>, user: User, accessToken: String) =
        externalVoteGateway.upVote(votes, user.id, accessToken)
            .flatMap { Observable.fromIterable(votes) }
            .flatMap { update(it) }
            .map { }

    private fun setAsProcessed(vote: Vote) =
        vote.apply { this.processed = true }

    private fun update(vote: Vote) =
        internalVoteGateway.update(vote)

    fun vote(vote: Vote) =
        internalVoteGateway.vote(vote)

    fun vote(suggestion: Suggestion) =
        userProvider.provide()
            .flatMap { vote(toVote(suggestion, it)) }

    private fun toVote(
        suggestion: Suggestion,
        it: User
    ) = suggestion.toVote(it.id, false)

    fun delete(vote: Vote) =
        internalVoteGateway.delete(vote)

    fun delete(suggestion: Suggestion, user: User) =
        internalVoteGateway.delete(suggestion, user)

    fun getVotesForSubject(id: String): Observable<Vote> = userProvider
        .provide()
        .flatMap { getUserSubjectVotes(id, it.id) }

    private fun getUserSubjectVotes(subjectId: String, userId: String) =
        internalVoteGateway.getUserSubjectVotes(subjectId, userId)
}