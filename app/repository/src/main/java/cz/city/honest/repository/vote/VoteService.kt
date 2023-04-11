package cz.city.honest.repository.vote

import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.User
import cz.city.honest.dto.Vote
import cz.city.honest.service.gateway.internal.InternalVoteGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class VoteService(
    private val voteRepository: VoteRepository
) : InternalVoteGateway {

    override fun updateVotes(user: User): Flowable<Vote> =
        voteRepository.get(listOf(user.id))

    override fun update(vote: Vote): Observable<Unit> =
        voteRepository
            .update(vote)
            .map {  }

    override fun vote(vote: Vote): Observable<Unit> =
        voteRepository
            .insert(vote)
            .map { }

    override fun delete(vote: Vote): Observable<Unit> =
        voteRepository
            .delete(vote)
            .map { }

    override fun delete(suggestion: Suggestion, user: User): Observable<Unit> =
        voteRepository
            .delete(suggestion.id, user.id)
            .map { }

    override fun findBySuggestionId(suggestionId:String) = voteRepository.getBySuggestionId(suggestionId)

    override fun getUserSubjectVotes(subjectId: String, userId: String): Observable<Vote> =
        voteRepository
            .getBySubjectId(subjectId, userId)
            .toObservable()
}