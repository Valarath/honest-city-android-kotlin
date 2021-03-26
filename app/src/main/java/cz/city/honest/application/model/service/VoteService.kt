package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.Suggestion
import cz.city.honest.application.model.gateway.server.PostUpVoteRequest
import cz.city.honest.application.model.gateway.server.VoteServerSource
import cz.city.honest.application.model.repository.vote.VoteRepository
import cz.city.honest.mobile.model.dto.Vote
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class VoteService(
    val voteServerSource: VoteServerSource,
    val voteRepositories: Map<Class<out Vote>, @JvmSuppressWildcards VoteRepository<out Vote, out Suggestion>>
) : Updatable {

    override fun update(): Observable<Unit> =
        Flowable.fromIterable(voteRepositories.values)
            .flatMap { it.get(listOf()) }
            .toList()
            .map { PostUpVoteRequest(it.toList(), "") }
            .toObservable()
            .flatMap { voteServerSource.upVote(it) }

}