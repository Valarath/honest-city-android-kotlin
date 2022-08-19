package cz.city.honest.service.vote

import cz.city.honest.external.PostUpVoteRequest
import cz.city.honest.external.VoteServerSource
import cz.city.honest.service.RepositoryProvider
import cz.city.honest.service.update.PrivateUpdatable
import cz.city.honest.service.user.UserProvider
import cz.city.honest.dto.Suggestion
import cz.city.honest.dto.User
import cz.city.honest.dto.Vote
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

class VoteService(
    private val voteServerSource: VoteServerSource,
    private val voteRepositories: Map<String, @JvmSuppressWildcards cz.city.honest.repository.vote.VoteRepository<out Vote, out Suggestion>>,
    val userProvider: UserProvider
) : PrivateUpdatable {

    override fun update(accessToken: String): Observable<Unit> =
        userProvider.provide()
            .flatMap { updateVotes(it, accessToken) }


    //.onErrorComplete()

    private fun updateVotes(user: User, accessToken: String) =
        Flowable.fromIterable(voteRepositories.values)
            .flatMap { it.get(listOf(user.id)) }
            .map { setAsProcessed(it) }
            .toList()
            .toObservable()
            .flatMap { updateVotes(it,user,accessToken) }

    private fun updateVotes(votes: List<Vote>, user: User, accessToken: String) =
        Observable.just(
            PostUpVoteRequest(
                votes,
                user.id
            )
        )
            .map {
                PostUpVoteRequest(
                    votes,
                    user.id
                )
            }
            .flatMap { voteServerSource.upVote(it, accessToken) }
            .flatMap { Observable.fromIterable(votes) }
            .flatMap { update(it) }
            .map {  }

    private fun setAsProcessed(vote: Vote) =
        vote.apply { this.processed = true }

    private fun update(vote: Vote) =
        RepositoryProvider.provide(voteRepositories, vote::class.java)
            .update(vote)

    fun vote(vote: Vote) =
        RepositoryProvider.provide(voteRepositories, vote::class.java)
            .insert(vote)

    fun vote(suggestion: Suggestion) =
        userProvider.provide()
            .flatMap { vote(toVote(suggestion, it)) }

    private fun toVote(
        suggestion: Suggestion,
        it: User
    ) = suggestion.toVote(it.id, false)

    fun delete(vote: Vote) =
        RepositoryProvider.provide(voteRepositories, vote::class.java)
            .delete(vote)

    fun delete(suggestion: Suggestion, user: User): Observable<Int> =
        Observable.just(voteRepositories.values.first())
            .flatMap { it.delete(suggestion.id,user.id) }

    fun getVotesForSubject(id: String): Observable<Vote> = userProvider
        .provide()
        .flatMap { getUserSubjectVotes(id, it.id) }

    private fun getUserSubjectVotes(subjectId: String, userId: String) =
        Flowable.fromIterable(voteRepositories.values)
            .flatMap { it.getBySubjectId(subjectId, userId) }
            .toObservable()

}