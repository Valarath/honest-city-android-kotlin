package cz.city.honest.application.model.service

import cz.city.honest.application.model.dto.*
import cz.city.honest.application.model.gateway.server.PostUpVoteRequest
import cz.city.honest.application.model.gateway.server.VoteServerSource
import cz.city.honest.application.model.repository.vote.VoteRepository
import cz.city.honest.mobile.model.dto.*
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.time.LocalDate
import java.util.*

class VoteService(
    val voteServerSource: VoteServerSource,
    val voteRepositories: Map<String, @JvmSuppressWildcards VoteRepository<out Vote, out Suggestion>>,
    val userProvider: UserProvider
) : Updatable {

    override fun update(): Observable<Unit> =
        Flowable.fromIterable(voteRepositories.values)
            .flatMap { it.get(listOf()) }
            .toList()
            .map { PostUpVoteRequest(it.toList(), "") }
            .toObservable()
            .flatMap { voteServerSource.upVote(it) }

    fun vote(vote: Vote) =
        RepositoryProvider
            .provide(voteRepositories, vote::class.java)
            .insert(vote)

    fun delete(vote: Vote) =
        RepositoryProvider
            .provide(voteRepositories, vote::class.java)
            .delete(vote)

    fun getVotesForSubject(id: String): Observable<Vote> =
        userProvider.provide()
            .flatMap { Observable.fromIterable(getMockSuggestions(it.id)) }

    private fun getMockSuggestions(id: String): List<Vote> {
        return listOf(
            VoteForExchangePointDelete(
                ClosedExchangePointSuggestion(
                    UUID.randomUUID().toString(),
                    State.IN_PROGRESS,
                    5,
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
                ), id
            ),
            VoteForNewExchangePoint(
                NewExchangePointSuggestion(
                    UUID.randomUUID().toString(),
                    state = State.DECLINED,
                    votes = 6,
                    position = Position(55.0, 77.0),
                    suggestionId = UUID.randomUUID().toString()
                ), id
            ),
            VoteForExchangePointRateChange(
                ExchangeRateSuggestion(
                    UUID.randomUUID().toString(),
                    state = State.ACCEPTED,
                    votes = 10,
                    watchedSubjectId = UUID.randomUUID().toString(),
                    suggestedExchangeRate = ExchangeRate(
                        55,
                        Watched(LocalDate.now(), LocalDate.now()),
                        mutableSetOf(
                            Rate("CZK", ExchangeRateValues(22.0)),
                            Rate("USD", ExchangeRateValues(22.0))
                        )
                    ),
                    suggestionId = UUID.randomUUID().toString()
                ), id
            )
        )
    }

}