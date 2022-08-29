package cz.city.honest.repository.vote

import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.User
import cz.city.honest.dto.Vote
import cz.city.honest.repository.RepositoryProvider

import cz.city.honest.service.gateway.internal.InternalVoteGateway
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class VoteService(
    private val voteRepositories: Map<String, @JvmSuppressWildcards VoteRepository<out Vote, out Suggestion>>
) : InternalVoteGateway {

    override fun updateVotes(user: User): Flowable<out Vote> =
        Flowable.fromIterable(voteRepositories.values)
            .flatMap { it.get(listOf(user.id)) }

    override fun update(vote: Vote): Observable<Unit> =
        RepositoryProvider.provide(voteRepositories, vote::class.java)
            .update(vote)
            .map { }

    override fun vote(vote: Vote): Observable<Unit> =
        RepositoryProvider.provide(voteRepositories, vote::class.java)
            .insert(vote)
            .map { }

    override fun delete(vote: Vote): Observable<Unit> =
        RepositoryProvider.provide(voteRepositories, vote::class.java)
            .delete(vote)
            .map { }

    override fun delete(suggestion: Suggestion, user: User): Observable<Unit> =
        Observable.just(voteRepositories.values.first())
            .flatMap { it.delete(suggestion.id, user.id) }
            .map { }

    override fun getUserSubjectVotes(subjectId: String, userId: String): Observable<out Vote> =
        Flowable.fromIterable(voteRepositories.values)
            .flatMap { it.getBySubjectId(subjectId, userId) }
            .toObservable()
}